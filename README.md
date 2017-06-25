# matisse_kotlin
对matisse进行学习+定制，以满足实际项目需求

###[**原项目地址**](https://github.com/zhihu/Matisse) 

###使用说明
下载后，作为依赖项目进行使用

####增加修改内容如下：

2017-06-26

> 1.可显示隐藏标题栏，当显示时，和官方效果一致，隐藏时，默认底部状态栏会在中间部分增加`返回`，点击会进行返回:
>
>                                             Matisse
>                                             .from(this@SampleActivity)
>                                             .showToolbar(true)
> 2.可选择自定义目录:
>
>       									Matisse
>                                         .from(this@SampleActivity)
>                                         .customFolders(arrayOf("news_article"))
> 3.对特定图片或视频格式进行过滤(只展示所选择的类型，其他类型不进行显示):
>
>                                     Matisse
>                                     .from(this@SampleActivity)
>                                     .choose(MimeType.of(MimeType.GIF,MimeType.MP4)) // 用法和官方一致
> 4.默认只显示单一类型;
