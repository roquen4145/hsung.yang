import numpy as np
import cv2


def warpAffine():
	img = cv2.imread('transform.png')

	pts1 = np.float32([[50,50],[200,50],[20,200]])
	pts2 = np.float32([[70,100],[220,50],[150,250]])

	M = cv2.getAffineTransform(pts1,pts2)

	result = cv2.warpAffine(img,M,(350,300))

	cv2.imshow('original',img)
	cv2.imshow('Affine Transform',result)

	cv2.waitKey(0)
	cv2.destroyAllWindows()
	
if __name__ == '__main__':
	warpAffine()