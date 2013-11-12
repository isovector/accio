Accio
=====

## Getting Started

    sudo apt-get install scala
    wget http://downloads.typesafe.com/play/2.2.1/play-2.2.1.zip
    sudo unzip play-2.2.1.zip -d /opt
    sudo chown -R `whoami`:users /opt/play-2.2.1
    rm play-2.2.1.zip
    echo 'PATH=$PATH:/opt/play-2.2.1' >> ~/.bashrc
    source ~/.bashrc
    git clone git@github.com:Paamayim/accio.git
    cd accio
    play run

