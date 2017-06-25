# matisse_custom
对matisse进行学习+定制，以满足实际项目需求

###[**官方项目地址**](https://github.com/zhihu/Matisse) 

###使用说明
> 1.配置kotlin开发环境
>
> 2.下载后，作为依赖项目进行使用

---



**Task List**

- [x] 显示隐藏标题栏

- [x] 自定义目录

- [x] 过滤选择类型

- [ ] 拍照功能作为单一功能

- [ ] 拍照后照片同步

- [ ] 录制视频功能

- [ ] 其他文件管理


------

  


####增加修改内容如下：

2017-06-26

> 1.可显示隐藏标题栏，当显示时，和官方效果一致，隐藏时，默认底部状态栏会在中间部分增加`返回`，点击会进行返回:
>
>                                             Matisse
>                                             .from(this@SampleActivity)
>                                             .showToolbar(true)
> 2.可选择自定义目录(只匹配目录名，不进行全路径匹配):
>
>                                         Matisse.from(this@SampleActivity)
>                                         .customFolders(arrayOf("news_article"))
> 3.对特定图片或视频格式进行过滤(只展示所选择的类型，其他类型不进行显示):
>
>                                     Matisse
>                                     .from(this@SampleActivity)
>                                     .choose(MimeType.of(MimeType.GIF,MimeType.MP4)) // 用法和官方一致
>
