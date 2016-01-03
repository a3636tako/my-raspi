# 音楽ファイルを管理するサービス

## 概要
LAN内のサーバ上で音楽ファイルを管理します。

## 仕様
このサービスは以下のAPIを持ちます

### 管理されている音楽ファイルリストの取得

#### URL
 http://server-address/list

#### パラメータ
* album アルバム名で絞り込みます
* artist アーティスト名で絞り込みます
* title 曲名で絞り込みます

#### クエリの例
アーティスト名が"abc"で、アルバム名が"def"である音楽ファイルのリストを取得する場合

GET http://server-address/list?artist=abc&album=def
 
#### レスポンス
結果はJson形式で返されます

```
{
	"list" : [
		{
			"album_id" : 1,
			"title" : "def",
			"artist" : "abc",
			"year" : 2015,
			"audios" : [
				{
					"audio_id" : 1,
					"title" : "track1",
					"trackNumber" : 1
				},
				{
					"audio_id" : 2,
					"title" : "track2",
					"trackNumber" : 2
				}
			]
		}
	]
}
```
キー"list"はアルバム情報の配列になります。
各アルバム情報は次の値を持ちます

+ album_id
アルバムID

+ title 
アルバムのタイトル

+ artist
アーティスト名

+ year
発売年

+ audios
音楽ファイル情報の配列

各音楽ファイル情報は次の値を持ちます
+ audio_id
オーディオID

+ title
曲名

+ trackNumber
トラックナンバー

### 音楽ファイルの取得

#### URL
GET http://server-address/audio/[audio_id].mp3

GET http://server-address/audio/[artist]/[album]/[title].mp3	

#### パラメータ
* audio_id 音楽情報を取得して得られたオーディオID
* artist アーティスト名
* album アルバム名
* title 曲名

#### クエリの例
オーディオIDが"3"の曲を取得する場合

 http://server-address/audio/3.mp3
 
アーティスト"abc"、アルバム名"def"、曲名"ghi"の曲を取得する場合

 http://server-address/audio/abc/def/ghi.mp3
 
#### レスポンス
指定された曲が存在する場合、その音楽データ


### 音楽ファイルを管理対象に追加する
#### URL
POST http://server-address/audio

#### データ
追加したい音楽データ(MP3)をbodyとして送信します


## 使用したフレームワーク
Play Framework 2.4.6 (Java)

## 使用した主なライブラリ
+ Ebean 
　　O/Rマッピング
+ Jackson JSON Processor 
　　Jsonのパース、生成
+ JAudiotagger
   MP3ファイルのタグ解析
   
## 工夫した点
+ HTTPを使った簡単なAPIとすることで、パソコンで音楽を取り込んでスマートフォンで聞くというようなことが、特殊なソフトやアプリを入れなくてもできること。
+ まずテストを書いてからコードを書いたこと。