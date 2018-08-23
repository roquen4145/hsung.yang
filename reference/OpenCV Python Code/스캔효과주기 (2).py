import numpy as np
import cv2

def adaptive_threshold():
	imgfile = 'document.jpg'
	img = cv2.imread(imgfile,cv2.IMREAD_GRAYSCALE)

	r = 600.0 / img.shape[0]
	dim = (int(img.shape[1]*r),600)
	img = cv2.resize(img,dim,interpolation = cv2.INTER_AREA)

	blur = cv2.GaussianBlur(img,(9,9),0)

	result_without_blur = cv2.adaptivethreshold(img,255,cv2.ADAPTIVE_THRES_MEAN_C,cv2.THRESH_BINARY)
	result_with_blur = cv2.adaptiveThreshold(blur,255,cv2.ADAPTIVE_THRES_MEAN_C,cv2.THRESH_BINARY)
	cv2.imshow('Without Blur',result_without_blur)
	cv2.imshow('With Blur',result_with_blur)

	cv2.waitKey(0)
	cv2.destroyAllWindows()
		
if __name__ == '__main__':
	global_threshold()