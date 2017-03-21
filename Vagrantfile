# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
VAGRANTFILE_API_VERSION = "2"
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  # configure proxy if necessary
  if Vagrant.has_plugin?("vagrant-proxyconf")
    config.proxy.http     = "http://proxy:8080"
    config.proxy.https    = "http://proxy:8080"
    config.proxy.no_proxy = "/var/run/docker.sock,localhost,127.0.0.1"
    config.proxy.enabled  = true
  end

  # name of the box
	config.vm.box = "ubuntu/xenial64"
  # setting hostname
	config.vm.hostname = "BootplateDevOps"
  # set up public_network
	config.vm.network "public_network"

  # forwarded ports
    config.vm.network "forwarded_port", guest: 9000, host: 9000

  # VirtualBox Specific Customization
    config.vm.provider "virtualbox" do |vb|
		# Use VBoxManage to customize the VM. For example to change memory:
		vb.customize ["modifyvm", :id, "--memory", "2048"]
    end

  # running script shell
  config.vm.provision "shell", inline: <<-SHELL
  # add additional package repositories
  echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
  apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
  curl -sL https://deb.nodesource.com/setup_6.x | bash -

  # upgrade system
  apt-get update
  apt-get -y upgrade

  # Install java JDK and SBT
  apt-get -y install openjdk-8-jdk sbt nodejs
  update-alternatives --config java

  # install gulp
  npm install -g gulp-cli

  # sbt environment variables
  echo 'export SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxMetaspaceSize=1024m"' >> /home/ubuntu/.bashrc

  SHELL

end
