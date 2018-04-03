#include <jni.h>
#include <traildb.h>
#include <inttypes.h>
#include <memory.h>
#include "traildb_TrailDB.h"
#include "traildb_TrailDBCursor.h"
#include "traildb_constructor_TrailDBNativeConstructor.h"


// API for writing the data (constructor)
JNIEXPORT jlong JNICALL Java_traildb_constructor_TrailDBNativeConstructor_open
        (JNIEnv *env, jobject obj, jstring path, jobjectArray fields) {
    tdb_cons *pointer = tdb_cons_init();
    if (!pointer) {
        return (*env)->ThrowNew(env, (*env)->FindClass(env, "traildb/TrailDBException"),
                                "Failed to init TrailDB API");
    }

    const char *path_chars = (*env)->GetStringUTFChars(env, path, 0);

    int string_count = (*env)->GetArrayLength(env, fields);
    char *string_array[string_count];

    for (int i = 0; i < string_count; i++) {
        jstring string = (jstring) ((*env)->GetObjectArrayElement(env, fields, i));
        const char *raw_string = (*env)->GetStringUTFChars(env, string, 0);
        string_array[i] = (char *) raw_string;
    }

    tdb_error open = tdb_cons_open(pointer, path_chars, (const char **) string_array, (uint64_t) string_count);
    if (open != TDB_ERR_OK) {
        tdb_cons_close(pointer);
        return (*env)->ThrowNew(env, (*env)->FindClass(env, "traildb/TrailDBException"),
                                tdb_error_str(open));
    }
    return (jlong) pointer;
}

// adding a value to constructor
JNIEXPORT jlong JNICALL Java_traildb_constructor_TrailDBNativeConstructor_add
        (JNIEnv *env, jobject obj, jlong pointer, jbyteArray id, jlong timestamp, jobjectArray data) {
    tdb_cons *constructor = (tdb_cons *) pointer;
    jbyte *const id_byte_array = (*env)->GetByteArrayElements(env, id, 0);

    // creating array of strings
    int data_length = (*env)->GetArrayLength(env, data);

    char *value_array[data_length];
    uint64_t length_array[data_length];
    jstring *to_free[data_length];

    for (int i = 0; i < data_length; i++) {

        // get internal object reference
        jstring string = (jstring) ((*env)->GetObjectArrayElement(env, data, i));
        to_free[i] = &string;

        // get string length
        jsize string_length = (*env)->GetStringUTFLength(env, string);
        // get pointer to chars
        jboolean copy = JNI_TRUE;
        const char *raw_string = (*env)->GetStringUTFChars(env, string, &copy);

        value_array[i] = (char *) raw_string;
        length_array[i] = (uint64_t) string_length;
    }

    tdb_error err = tdb_cons_add(constructor, (const uint8_t *) id_byte_array, (const uint64_t) timestamp,
                                 (const char **) value_array, (const uint64_t *) length_array);

    for (int i = 0; i < data_length; i++) {
//        (*env)->ReleaseStringUTFChars(env, *to_free[i], value_array[i]);
    }

    if (err != TDB_ERR_OK) {
        return (*env)->ThrowNew(env, (*env)->FindClass(env, "traildb/TrailDBException"),
                                tdb_error_str(err));
    }
    return 0;
}

// closing constructor resource
JNIEXPORT jlong JNICALL Java_traildb_constructor_TrailDBNativeConstructor_close
        (JNIEnv *env, jobject obj, jlong pointer) {
    tdb_cons *constructor = (tdb_cons *) pointer;
    tdb_error err = tdb_cons_finalize(constructor);
    tdb_cons_close(constructor);
    if (err != TDB_ERR_OK) {
        return (*env)->ThrowNew(env, (*env)->FindClass(env, "traildb/TrailDBException"),
                                tdb_error_str(err));
    }
    return 0;
}


// TrailDB read section
// Opens traildb for reading
JNIEXPORT jlong JNICALL Java_traildb_TrailDB_openDb
        (JNIEnv *env, jobject obj, jstring path) {
    tdb *tdb = tdb_init();
    if (!tdb) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/OutOfMemoryError"),
                         "Failed to allocate TrailDB memory");
        return 0;
    }
    const char *path_chars = (*env)->GetStringUTFChars(env, path, (jboolean) 0);
    tdb_error open_result = tdb_open(tdb, path_chars);
    (*env)->ReleaseStringUTFChars(env, path, path_chars); // @todo check, whether copy needed

    if (open_result != TDB_ERR_OK) {
        tdb_close(tdb);
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/io/IOException"), tdb_error_str(open_result));
        return 0;
    }

    return (jlong) tdb;
}

JNIEXPORT jobject JNICALL Java_traildb_TrailDB_getMeta
        (JNIEnv *env, jobject obj, jlong pointer) {

    tdb *tdb_pointer = (tdb *) pointer;

    jclass meta_class = (*env)->FindClass(env, "traildb/TrailDBMeta");
    if (!meta_class) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/RuntimeException"),
                         "Class 'traildb.TrailDBMeta' not found");
        return 0;
    }
    jmethodID method = (*env)->GetMethodID(env, meta_class, "<init>", "(JJJJJ)V");
    if (!method) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/RuntimeException"),
                         "Failed to find 'traildb.TrailDBMeta' constructor");
        return 0;
    }

    jobject retval = (*env)->NewObject(env, meta_class, method,
                                       (jlong) tdb_num_trails(tdb_pointer),
                                       (jlong) tdb_num_events(tdb_pointer),
                                       (jlong) tdb_num_fields(tdb_pointer),
                                       (jlong) tdb_min_timestamp(tdb_pointer),
                                       (jlong) tdb_max_timestamp(tdb_pointer));
    if (!retval) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/RuntimeException"),
                         "Failed to create 'traildb.TrailDBMeta'");
        return 0;
    }
    return retval;
}



// Get UUID value for particular trail_id
JNIEXPORT jbyteArray JNICALL Java_traildb_TrailDB_getUUID
        (JNIEnv *env, jobject obj, jlong pointer, jlong trail_id) {
    const uint8_t *uuid = tdb_get_uuid((tdb *) pointer, (uint64_t) trail_id);
    if (!uuid) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "traildb/TrailDBException"),
                         "Invalid trail_id given");
        return 0;
    }

    jbyteArray arr = (*env)->NewByteArray(env, 16);
    (*env)->SetByteArrayRegion(env, arr, 0, 16, (const jbyte *) uuid);
    return arr;

}

// Cursor section
// create new cursor
JNIEXPORT jlong JNICALL Java_traildb_TrailDBCursor_constructor
        (JNIEnv *env, jobject obj, jlong ptr) {
    tdb *tdb_pointer = (tdb *) ptr;
    tdb_cursor *cursor = tdb_cursor_new(tdb_pointer);
    if (!cursor) {
        return (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/OutOfMemoryError"),
                                "Failed to allocate TrailDB memory");
    }

    // install cursor to initial position
    tdb_error err = tdb_get_trail(cursor, 0);

    if (err != TDB_ERR_OK) {
        return (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/OutOfMemoryError"),
                                "Failed to reset cursor");
    }

    return (jlong) cursor;
}

// cursor peek
JNIEXPORT void JNICALL Java_traildb_TrailDBCursor_peek
        (JNIEnv *env, jobject obj, jlong pointer) {

    tdb_cursor *cursor = (tdb_cursor *) pointer;
}

