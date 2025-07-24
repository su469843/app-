#!/bin/bash

# 检查是否提供了所有参数
if [ "$#" -ne 4 ]; then
    echo "Usage: $0 <keystore-name> <alias> <keystore-password> <key-password>"
    exit 1
fi

KEYSTORE_NAME=$1
ALIAS=$2
KEYSTORE_PASSWORD=$3
KEY_PASSWORD=$4

# 生成密钥库
keytool -genkeypair \
  -v \
  -keystore $KEYSTORE_NAME \
  -alias $ALIAS \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass $KEYSTORE_PASSWORD \
  -keypass $KEY_PASSWORD \
  -dname "CN=Markdown Editor,OU=Development,O=QZZ,L=Unknown,ST=Unknown,C=CN"

# 将密钥库转换为 base64
base64 $KEYSTORE_NAME > ${KEYSTORE_NAME}.base64

echo "Keystore has been generated and encoded as base64"
echo "Please add the following secrets to your GitHub repository:"
echo "KEYSTORE: $(cat ${KEYSTORE_NAME}.base64)"
echo "KEY_ALIAS: $ALIAS"
echo "KEYSTORE_PASSWORD: $KEYSTORE_PASSWORD"
echo "KEY_PASSWORD: $KEY_PASSWORD"
