# LargeImageSample
加载长图

# 效果
![image](https://github.com/CoderWalterXu/LargeImageSample/blob/master/screenshot/loadlargeImage.gif)

# 思路
将要加载的图片，以宽度为标准，沿着对角线缩放至刚好能在屏幕上显示图片的宽度，
# 要点
* 手势识别 GestureDetector
* Bitmap相关配置 BitmapFactory.Options()
* 区域解码 BitmapRegionDecoder

