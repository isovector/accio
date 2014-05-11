Accio
=====

## Getting Started

Windows:

    download scala @ http://scala-lang.org/download/2.9.2.html
    add scala-2.7.1.final\bin to PATH environment variable
    download play @ http://downloads.typesafe.com/play/2.2.1/play-2.2.1.zip
    extract to location you wish to store binar
    add __parent_directory__\play-2.2.1 to PATH environment variable
    git clone git@github.com:Paamayim/accio.git
    cd accio
    play run

Linux:

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

