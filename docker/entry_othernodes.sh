#!/bin.bash
# Define the directory to store authorized_keys locally
LOCAL_SSH_DIR="/root/.ssh"
AUTHORIZED_KEYS="$LOCAL_SSH_DIR/authorized_keys"

ls /shared
cat /shared/id_rsa.pub >> $AUTHORIZED_KEYS

chmod 600 $AUTHORIZED_KEYS
# Ensure correct permissions for the authorized_keys file

exec /usr/sbin/sshd -D -e