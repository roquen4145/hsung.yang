import numpy as np
import cv2

def handle_image():
	imgfile = 'sample.png'
	img = cv2.imread(imgfile, cv2.IMREAD_COLOR)

	cv2.imshow('image',img)
	cv2.waitKey(0)
	cv2.destroyAllWindows()

if __name__ == '__main__':
	handle_image()