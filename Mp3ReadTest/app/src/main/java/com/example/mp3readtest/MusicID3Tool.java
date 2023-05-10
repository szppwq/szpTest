package com.example.mp3readtest;

import android.util.Log;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.IOException;

public class MusicID3Tool {
    private Mp3File mp3File = null;

    public void setMusicFilePath(String musicPath){
        try {
            mp3File = new Mp3File(musicPath);
        } catch (IOException e) {
            Log.e("szp","IOException"+e);
            throw new RuntimeException(e);
        } catch (UnsupportedTagException e) {
            Log.e("szp","UnsupportedTagException"+e);
            throw new RuntimeException(e);
        } catch (InvalidDataException e) {
            Log.e("szp","InvalidDataException"+e);
            throw new RuntimeException(e);
        }
        Log.e("szp","mp3file.hasId3v1Tag()"+mp3File.hasId3v1Tag());
        Log.e("szp","?"+mp3File.hasId3v1Tag());
        if (mp3File.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3File.getId3v1Tag();
        }
        if (mp3File.hasId3v2Tag()){
            ID3v2 id3v2Tag = mp3File.getId3v2Tag();
            Log.e("szp","唱片歌曲数量: " + id3v2Tag.getTrack());
            Log.e("szp","艺术家: " + id3v2Tag.getArtist());
            Log.e("szp","歌曲名: " + id3v2Tag.getTitle());
            Log.e("szp","唱片名: " + id3v2Tag.getAlbum());
            Log.e("szp","歌曲长度:" + mp3File.getLengthInMilliseconds() + "秒");
            Log.e("szp","码率: " + mp3File.getBitrate());
            Log.e("szp","专辑插画类型" + id3v2Tag.getAlbumArtist());
            Log.e("szp","发行时间: " + id3v2Tag.getYear());
//            Log.e("szp","流派: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
//            Log.e("szp","注释: " + id3v2Tag.getComment());
            Log.e("szp","歌词: " + id3v2Tag.getLyrics());
            Log.e("szp","作曲家: " + id3v2Tag.getComposer());
//            Log.e("szp","发行公司: " + id3v2Tag.getPublisher());
//            Log.e("szp","Original artist: " + id3v2Tag.getOriginalArtist());
//            Log.e("szp","Album artist: " + id3v2Tag.getAlbumArtist());
//            Log.e("szp","版权: " + id3v2Tag.getCopyright());
//            Log.e("szp","URL: " + id3v2Tag.getUrl());
            Log.e("szp","编码格式: " + id3v2Tag.getEncoder());

            //专辑插画
            byte[] albumImageData = id3v2Tag.getAlbumImage();
//            Log.e("szp","albumImageData"+albumImageData);
            if (albumImageData != null) {
                Log.e("szp","专辑插图长度: " + albumImageData + " bytes");
                Log.e("szp","专辑插图类型: " + id3v2Tag.getAlbumImageMimeType());
            }
        }
    }
}
