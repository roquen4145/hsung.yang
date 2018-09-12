# 연구작품개요

## 작품 주제

한글필기의 디지털문서화 개선

### 세부 설명

현재 한글필기 인식기술과 이미지 보정 기술 자체는 많이 개발되었지만 그 기술들이 일반 사용자들이 사용할 정도로 통합되지 않았다.

따라서 본 연구작품에서는 한글필기의 디지털문서화 과정을 개선하여 일반 사용자들이 실생활에서도 사용할 수 있도록 기술이 적용되는 분야를 확장하고 추가적인 기능을 탑재한다.


## 작품 진행 계획

1. 한글 글자 인식

	- 글자 인식 방법 결정

	- 필기 테스트 데이터 생성

	- 글자 인식 테스트

2. 필기 라인 인식

3. 필기 들여쓰기 인식

4. 필기 교정부호 인식

5. 필기 목록기호 인식

6. 개인 글씨체 인식

## 개선사항

* 모바일 애플리케이션 개발

	빠르고 쉬운 사용을 위해 모바일 어플리케이션을 개발한다.

* 문서 형식 보존

	원래의 문서의 형태를 텍스트로 변환한 후에도 유지할 수 있도록 한다.


## 이슈

* 한글 글자의 인식 방법

	a. 기존 개발된 기술 조사

		- 이미지에서 글자 인식 오픈소스 ( https://github.com/tesseract-ocr/tesseract )

			Apache License 2.0

			API examplie == https://github.com/tesseract-ocr/tesseract/wiki/APIExample 

		- 머신러닝 ( https://bi.snu.ac.kr/Publications/Conferences/Domestic/KCC2016_BHKim.pdf )

		- python 라인 인식 기술

		- 맞춤법 판단 및 자동수정

	b. 적용가능 여부 판단
	
		- Tesseract의 HOCR code 사용 ( HTML formatted OCR )

	c. 기술 적용 순서 


		ㄱ. 기존 방안

			- 이미지 전 처리 과정 ( Noise 제거 , 회전 , 배경색 제거 등)

			- 이미지 Text 영역 분리 ( Paragraph , List 등으로 분리)

			- Text의 각 영역별을 OCR로 데이터화

			- Format에 맞춰 정렬

			- String 값에 대해 맞춤법 판단 기술 적용 및 자동 수정


		ㄴ. 이미지 재배열 방안 ( 전자 필기 앱으로 만들어진 PDF 파일 or 이미지 파일을 input으로 )

			- 이미지 전 처리 과정

			- 이미지 Text 영역 분리 ( Paragraph , Block , Word 등 )

			- 영역별로 정렬

			- 이미지 수정 방법 고안



	d. 개발 순서

		- Tesseract 한글 데이터 적용 ( 2018/07/09 DONE )

		- Tesseract Sample Image Generation and Extraction ( 2018/07/09 Done , Replace to Google API )

		- Tesseract HOCR 데이터 출력 확인 ( 2018/07/10 Aborted )

		- Google Vision API Test ( 2018/07/31 Done )

		- 이미지 영역별 분리 ( Paragraph, List 등) , 영역 별 Format 저장 ( Supported by Google Vision API )

		- 중간 점검 미팅 ( 2018/07/31 Done )

		- 이미지 재배열 방식 구상 ( 2018/08/03 Done )

		- Google Vision API Test on HandWriting ( 2018/08/15 Done )

		- OpenCV를 이용한 이미지 재배열 방식 뼈대 만들기 ( 2018/08/15 Delayed )

		- OpenCV를 위한 새로운 프로젝트 생성 ( 2018/08/23 Done )

		- OpenCV 기본 예제 파일 컴파일 ( 2018/09/03 Done )

		- 사진 선택 및 파일 처리 ( 2018/09/05 Done )

		- 사진 크롭 ( 2018/09/06 Delayed )

		- 구글 클라우드 API 적용 ( 2018/09/06 Done )

		- 인식된 텍스트 출력 방법 설정 : PDF 출력 ( 2018/09/11 Done )

		- 저장장치 권한 문제 : ExifInterface ( 2018/09/12 Processing )

		- Noise가 추가된 데이터에 대한 출력 확인

		- 맞춤법 기술 적용 / 쉬운 적용방법 선택


	e. 기술 적용 이후 추가 사항



* 테스트 데이터 생성


	a. 인쇄물


	b. 필기 데이터 


* 컴파일 에러

	a. package android.hardware.camera2 is not exist

		Open Module Settings > set 'Compile Sdk Version' over API25

	b. ../../../../build/intermediates/cmake/debug/obj/arm64-v8a/libnative-lib.so', missing and no known rule to make it

		OpenCV 라이브러리 파일 복사 


* 이미지 편집 불가

	Editing is not supported for this Image.

	File error accessing recents directory (directory doesn't exist?)

	=> Intent.FLAG_GRANT_READ_URI_PERMISSION , Intent.FLAG_GRANT_WRITE_URI_PERMISSION

* 

## 참고 자료


OCR Programs

http://gongboobub.tistory.com/8

문자라인, 단어개수 검출 방법

https://wiki.studydev.com/pages/viewpage.action?pageId=11370546

Image Enhancements

https://stackoverflow.com/questions/9480013/image-processing-to-improve-tesseract-ocr-accuracy


구글 API 안드로이드 코드 참조 / 문서


https://github.com/GoogleCloudPlatform/cloud-vision

https://cloud.google.com/vision/docs/ocr


카메라 어플리케이션 만들기 참조

http://jeongchul.tistory.com/287


OpenCV on Android

http://www.hellogohn.com/post_one271


iText로 PDF 파일 만들기
http://blog.naver.com/PostView.nhn?blogId=dpdpwl75&logNo=221022542816&beginTime=0&jumpingVid=&from=search&redirect=Log&widgetTypeCall=true