import numpy as np
import cv2

def handle_image():
	imgfile = 'sample.png'
	img = cv2.imread(imgfile, cv2.IMREAD_GRAYSCALE)

	cv2.namedWindow('image', cv2.WINDOW_NORMAL)
	cv2.imshow('image',img)
	k = cv2.waitKey(0)

	if k == 27:
		cv2.destroyAllWindows()
	elif k == ord('s'):
		cv2.imwrite('grayimage.png',img)
		cv2.destroyAllWindows()

if __name__ == '__main__':
	handle_image()