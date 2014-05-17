#include <stdio.h>
#include "GArpSend.h"

JNIEXPORT void JNICALL Java_GArpSend_reply 
(JNIEnv *env, jobject this , jobject niface) {
    jclass cls = (*env)->GetObjectClass ( env, niface );
    jmethodID getInetAddresses = 
        (*env)->GetMethodID( env , cls , "getInetAddresses",
                             "()Ljava/util/Enumeration;");

    if ( getInetAddresses == NULL ) {
        printf (" could not obtain methodID getInetAddresses");
    }

    const jclass enumeration = (*env)->FindClass(env,"java/util/Enumeration");

    const jclass inetaddress = (*env)->FindClass(env,"java/net/InetAddress");

    const jmethodID hasMoreElements = 
        (*env)->GetMethodID(env,enumeration, "hasMoreElements", "()Z");
    

    const jmethodID nextElement = 
        (*env)->GetMethodID(env,enumeration, "nextElement", 
                            "()Ljava/lang/Object;");

        jobject inetAddresses = 
            (*env)->CallObjectMethod(env,niface,getInetAddresses );


        for (; (*env)->CallBooleanMethod(env,
                                   inetAddresses,hasMoreElements) == JNI_TRUE;)
        {

            jobject inetAddress = 
                (*env)->CallObjectMethod(env,inetAddresses,
                                         nextElement);
            
            jmethodID toString = (*env)->GetMethodID(env,inetaddress,
                                                     "toString",
                                                     "()Ljava/lang/String;");
            if ( toString == NULL ) {
                printf(" could not get methodID toString \n");
            }
                                                     
            jstring str = 
                (*env)->CallObjectMethod(env,inetAddress,toString);
            
            const char *c_str = (*env)->GetStringUTFChars(env,
                                                          str,NULL);
            printf (" toString: %s \n", c_str);
                                                           
        }

}
