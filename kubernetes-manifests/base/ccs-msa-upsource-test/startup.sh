#!/bin/bash
# Wrapper to start the upsource docker container. It checks if the persisted conf folder is present and creates a symlink to Upsource/conf. Then starts the upsource server.

PERSISTENT_DISK_MOUNT_POINT=/opt/upsource-disk

function run() {
        echo Running $*
        $*
        STATUS=$?
        if [ $STATUS -ne 0 ]; then
                echo $1 FAILED with status $STATUS
                exit $STATUS
        fi
        echo $1 SUCCESS
}

# copy conf folder if not yet exists
if [ -d "/opt/upsource-disk" ]; then

    # ------------------------ conf
    if [ ! -d "${PERSISTENT_DISK_MOUNT_POINT}/conf" ]; then
      echo "* Copying the conf folder as it does not yet exist."
      run cp -r /opt/Upsource/conf ${PERSISTENT_DISK_MOUNT_POINT}
    fi

    # delete local conf folder and create symlink on persistent disk
    echo "* Removing local conf folder"
    run rm -rf /opt/Upsource/conf
    echo "* Creating symlink to ${PERSISTENT_DISK_MOUNT_POINT}/conf"
    run ln -s ${PERSISTENT_DISK_MOUNT_POINT}/conf /opt/Upsource/conf

    # ----------------------- data
    if [ ! -d "${PERSISTENT_DISK_MOUNT_POINT}/data" ]; then
      echo "* Copying the data folder as it does not yet exist."
      run cp -r /opt/Upsource/data ${PERSISTENT_DISK_MOUNT_POINT}
    fi

    # delete local data folder and create symlink on persistent disk
    echo "* Removing local data folder"
    run rm -rf /opt/Upsource/data
    echo "* Creating symlink to ${PERSISTENT_DISK_MOUNT_POINT}/data"
    run ln -s ${PERSISTENT_DISK_MOUNT_POINT}/data /opt/Upsource/data


    # ----------------------- logs
    if [ ! -d "${PERSISTENT_DISK_MOUNT_POINT}/logs" ]; then
      echo "* Copying the logs folder as it does not yet exist."
      run cp -r /opt/Upsource/logs ${PERSISTENT_DISK_MOUNT_POINT}
    fi

    # delete local logs folder and create symlink on persistent disk
    echo "* Removing local logs folder"
    run rm -rf /opt/Upsource/logs
    echo "* Creating symlink to ${PERSISTENT_DISK_MOUNT_POINT}/logs"
    run ln -s ${PERSISTENT_DISK_MOUNT_POINT}/logs /opt/Upsource/logs



    # ----------------------- backups
    if [ ! -d "${PERSISTENT_DISK_MOUNT_POINT}/backups" ]; then
      echo "* Copying the backups folder as it does not yet exist."
      run cp -r /opt/Upsource/backups ${PERSISTENT_DISK_MOUNT_POINT}
    fi

    # delete local backups folder and create symlink on persistent disk
    echo "* Removing local backups folder"
    run rm -rf /opt/Upsource/backups
    echo "* Creating symlink to ${PERSISTENT_DISK_MOUNT_POINT}/backups"
    run ln -s ${PERSISTENT_DISK_MOUNT_POINT}/backups /opt/Upsource/backups


fi


echo "* Starting the Upsource server"
run /opt/Upsource/bin/upsource.sh run
