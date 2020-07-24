import React from 'react';
import {StyleSheet, Text, View, TouchableOpacity} from 'react-native';
import {ad} from 'react-native-ad';

export default function DrawFeed() {
  return (
    <View style={styles.container}>
      <ad.DrawFeed
        appid="5016582"
        codeid="945339778" // 广告位 codeid （必传），注意区分 Android 和 IOS
        express={true} // isExpress 用来区分是否用原生方式渲染（非必传），默认值：true
        onLoad={smg => {
          // 广告加载成功回调
          console.log('头条 Draw Feed 广告加载成功！', smg);
        }}
        onError={err => {
          // 广告加载失败回调
          console.log('头条 Draw Feed 广告加载失败！', err);
        }}
        onClick={val => {
          // 广告点击回调
          console.log('头条 Draw Feed 广告被用户点击！', val);
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});