
# SweeperMap
扫地机器人地图的自定义View，支持类似于百度地图的手势缩放功能。

![地图](./img/pic.png)

##使用方式
1. 引入XML布局
```
<com.xxx.xxx.AreaRoomView
   android:id="@+id/area_view"
   android:layout_width="match_parent"
   android:layout_height="300dp"
   android:background="@color/white" />
```

2. 设置数据
```
AreaRoomView areaRoom = findViewById(R.id.area_view);
List<AreaBean> areaBeans = new ArrayList<>();
//AreaBean对应的参数为，坐标点的id,x坐标，y坐标，障碍物标识符(0无障碍物，1有障碍物)
areaBeans.add(new AreaBean(1,1,1,1));
areaBeans.add(new AreaBean(2,1,2,1));
areaBeans.add(new AreaBean(3,2,2,1));
//设置数据，true为向地图里追加，false为刷新地图从头开始追加数据
areaRoom.setDatas(areaBeans, true);
```

3. 清除数据
```
areaRoom.clear();
```
