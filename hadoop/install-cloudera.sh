#!/usr/bin/env bash

function ensure_add_apt_installed()
{
    which add-apt-repository > /dev/null
    if [ $? -ne 0 ]; then
        apt-get install python-software-properties 
	echo "INFO $(date), apt-get-repository has been installed successfully!"
    else
	echo "INFO $(date), apt-get-repository has already been installed."
    fi  
}

function ensure_java_installed()
{
    which java > /dev/null
    code=$?
    if [ $code -ne 0 ]; then
	ensure_add_apt_installed
	add-apt-repository ppa:sun-java-community-team/sun-java6
	apt-get update 
        echo sun-java6-jdk shared/accepted-sun-dlj-v1-1 boolean true | debconf-set-selections
	apt-get install -y sun-java6-jdk 
	echo "INFO $(date), java has been installed successfully!"
    else
	echo "INFO $(date), java has already been installed."
    fi
} 

function ensure_curl_installed()
{
    which curl > /dev/null
    code=$?
    if [ $code -ne 0 ]; then
	apt-get install -y curl 
	echo "INFO $(date), curl has been installed successfully!"
    else
	echo "INFO $(date), curl has already been installed."
    fi
}

# add cloudera repository
function ensure_repository_added()
{
    name=`lsb_release -c | cut -f 2-2`

    case $name in
	lucid|maverick)  # ubuntu 10.04 | 10.10
	        repository_file="/etc/apt/sources.list.d/cloudera_$name.list"
		    if [ ! -f $repository_file ] ; then
			echo "deb http://archive.cloudera.com/debian $name-cdh3 contrib" | tee -a $repository_file
			echo "deb-src http://archive.cloudera.com/debian $name-cdh3 contrib" | tee -a $repository_file
			ensure_curl_installed
			curl -s http://archive.cloudera.com/debian/archive.key | apt-key add -
			apt-get update 
			    else
			echo "INFO $(date), cloudera repository already exists in /etc/apt/sources.list.d/"
			    fi
		        ;;
	*)
    echo "ERROR $(date), ubuntu version not supported."
    esac
}

function usage()
{
    echo "Usage: ./prog [[--help|-h]|[package]]"
    packages=`cat /var/lib/apt/lists/*cloudera*Packages | grep ^Package | cut -f 2-2 -d " " | tr '\n' ' '`
    echo $packages
}

function main()
{
    ensure_java_installed
    ensure_repository_added    

    packages=`cat /var/lib/apt/lists/*cloudera*Packages | grep ^Package | cut -f 2-2 -d " " | tr '\n' ' '`
    for package in $packages;
    do 
	if [ $1 = $package ] ; then
	        apt-get -y install $1 
            echo "install $package completed"
	        return 0
		fi
    done

    echo "$1 is not in cloudera repository."
    return 1
}

# check euid.
if [ $EUID -ne 0 ]; then
   echo "This script must be run as root" 
   exit 1
fi

if [ -z $1 ]; then
    usage
    exit 1
fi

case $1 in 
    --help|-h)usage ;;
    *)main $1 ;;
esac
