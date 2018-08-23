import numpy as np
import cv2

def nothing(x):
	pass

def global_threshold():
	imgfile = 'document.jpg'
	img = cv2.imread(imgfile,cv2.IMREAD_GRAYSCALE)

	r = 600.0 / img.shape[0]
	dim = (int(img.shape[1]*r),600)
	img = cv2.resize(img,dim,interpolation = cv2.INTER_AREA)

	WindowName = "Window"
	TrackbarName "Threshhold"

	cv2.namedWindow(WindowName)
	cv2.createTrackbar(TrackbarName,WindowName,50,255,nothing)

	Threshold = np.zeros(img.shape.np.uint8)

	while True:
		TrackbarPos = cv2.getTrackbarPos(TrackbarName,WindowName)
		cv2.threshold(img,TrackbarPos, 255, cv2.THRESH_BINARY , Threshold)
		cv2.imshow(WindowName , Threshold)

		k = cv2.waitKey(0)
		if k == 27:
			cv2.destroyAllWindows()
			cv2.waitkey(1)
			break
	return
		
if __name__ == '__main__':
	global_threshold()