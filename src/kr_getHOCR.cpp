#include <tesseract/baseapi.h>
#include <leptonica/allheaders.h>
#include <fstream>
#include <iostream>
#include <cstring>


int main()

{
    char *outText;
    char tessdataPath[100] ="/home/roquen/repo/hsung.yang/reference";
    char filename[11] = "kr_test_01";
    char *imagename = (char*) malloc(sizeof(char) * (strlen(filename)+ 4) );
    char *htmlname = (char*) malloc(sizeof(char) * (strlen(filename)+ 5) );
    strcpy(imagename,filename);
    strcat(imagename,".png");
    strcpy(htmlname,filename);
    strcat(htmlname,".html");


    tesseract::TessBaseAPI *api = new tesseract::TessBaseAPI();
    // Initialize tesseract-ocr with English, with specifying tessdata path
    if (api->Init(tessdataPath, "kor")) {
        fprintf(stderr, "Could not initialize tesseract.\n");
        exit(1);
    }


    // Open input image with leptonica library
    Pix *image = pixRead(imagename);
    api->SetImage(image);
    // Get OCR result
    outText = api->GetHOCRText(0);
    printf("OCR output:\n%s", outText);

    std::ofstream outFile(htmlname);
    outFile << outText << std::endl;
    outFile.close();


    // Destroy used object and release memory
    api->End();
    delete [] outText;
    pixDestroy(&image);

    return 0;
}
