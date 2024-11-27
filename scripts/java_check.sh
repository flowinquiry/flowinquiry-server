#!/bin/bash

# Set the required Java version as a variable
REQUIRED_JAVA_VERSION=21

# Function to compare two versions
# Function to compare two versions
version_ge() {
    # Compare two versions properly by splitting them into arrays
    local ver1=$1
    local ver2=$2

    if [[ "$ver1" == "$ver2" ]]; then
        return 0
    fi

    ver1_arr=(${ver1//./ })
    ver2_arr=(${ver2//./ })

    for i in {0..2}; do
        if [[ ${ver1_arr[$i]:-0} -gt ${ver2_arr[$i]:-0} ]]; then
            return 0
        elif [[ ${ver1_arr[$i]:-0} -lt ${ver2_arr[$i]:-0} ]]; then
            return 1
        fi
    done

    return 0
}

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed."
    exit 1
fi

# Get full Java version info
java_full_version=$(java -version 2>&1)

# Get the installed Java version (major version)
java_version=$(echo "$java_full_version" | awk -F[\".] '/version/ {print $2}')

# Get Java home path
java_home=$(dirname $(dirname $(readlink -f $(which java))))

# Get Java vendor
java_vendor=$(java -XshowSettings:properties -version 2>&1 | grep 'java.vendor =' | awk -F '= ' '{print $2}')

# Get Java runtime environment
java_runtime=$(java -XshowSettings:properties -version 2>&1 | grep 'java.runtime.name =' | awk -F '= ' '{print $2}')

# Get Java VM name
java_vm=$(java -XshowSettings:properties -version 2>&1 | grep 'java.vm.name =' | awk -F '= ' '{print $2}')

# Check if the Java version is valid
if [ -z "$java_version" ]; then
    echo "Failed to determine the Java version."
    exit 1
fi

# Display the full Java information
echo "Java Version Information:"
echo "$java_full_version"
echo
echo "Java Vendor: $java_vendor"
echo "Java Runtime Environment: $java_runtime"
echo "Java VM Name: $java_vm"
echo "Java Home: $java_home"

# Check if the Java version is greater than or equal to the required version
if version_ge "$java_version" "$REQUIRED_JAVA_VERSION"; then
    echo "Java version is $java_version, which is greater than or equal to $REQUIRED_JAVA_VERSION."
    exit 0
else
    echo "Java version is $java_version, which is less than $REQUIRED_JAVA_VERSION."
    exit 1
fi