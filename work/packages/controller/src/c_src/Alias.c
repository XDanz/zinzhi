#include <jni.h>
#include "Alias.h"
/*
 * Class:     Alias
 * Method:    addAlias
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_Alias_addAlias
(JNIEnv *env , jobject thisObj, jstring ifaceName, jstring ipAddress) {
    const  char *ifaceNameCStr = 
        (*env)->GetStringUTFChars(env, ifaceName, NULL);
    const char *ipAddressCStr = (*env)->GetStringUTFChars(env, ipAddress, NULL);

    if (set_ip ( ifaceNameCStr, ipAddressCStr) ) {
        jclass newExcCls = (*env)->FindClass(env,"AliasException");
        if ( newExcCls == 0)
            return;
        
        (*env)->ThrowNew(env,newExcCls,"Failed thrown from c code");
        
    }
}

/*
 * Class:     Alias
 * Method:    removeAlias
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_Alias_removeAlias
(JNIEnv *env, jobject thisObj, jstring ifaceName) {
    const char *ifaceNameCStr = (*env)->GetStringUTFChars(env ,ifaceName, NULL);
    if ( del_ip ( ifaceNameCStr) ) {
        jclass newExcCls = (*env)->FindClass(env,"AliasException");
        if ( newExcCls == 0)
            return;
        
        (*env)->ThrowNew(env,newExcCls,"Failed thrown from c code");
        
    }
}

