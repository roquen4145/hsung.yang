sudo apt-get install g++ # or clang++ (presumably)
sudo apt-get install autoconf automake libtool
sudo apt-get install pkg-config
sudo apt-get install libpng-dev
sudo apt-get install libjpeg8-dev
sudo apt-get install libtiff5-dev
sudo apt-get install zlib1g-dev

sudo apt-get install tesseract-ocr

sudo apt-get install libleptonica-dev






compiling API Examples
https://github.com/tesseract-ocr/tesseract/wiki/APIExample
https://github.com/tesseract-ocr/tesseract/wiki/APIExample#compiling-c-api-programs-on-linux


g++ -o myprogram myprogram.cpp -llept -ltesseract

