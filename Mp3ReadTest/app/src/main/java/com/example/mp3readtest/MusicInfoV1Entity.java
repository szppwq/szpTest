package com.example.mp3readtest;


import android.util.Log;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

public class MusicInfoV1Entity {

    //歌曲名字
    String title;
    // 艺术家
    String artist;
    // 作曲家(ID3V1不支持这个字段)
    String composer;
    // 所属唱片
    String album;
    // 发行年
    String year;
    // 备注
    String comment;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 获取MP3文件信息
     *
     * @param path MP3文件对象
     */
    public static MusicInfoV1Entity getMusicInfoV1(String path) {
        if (path == null) {
            Log.e("szp","文件地址null");
        return null;
        }
        Log.e("szp","拿到文件地址开始解析"+new File(path));
        return getMusicInfoV1(new File(path));
    }

    public static MusicInfoV1Entity getMusicInfoV1(File musicFile) {
        if (musicFile == null) {
            Log.e("szp","获取路径下的文件null");
            return null;
        }
        MusicInfoV1Entity v1Entity;
        Log.e("szp","获取路径下的文件"+musicFile);
        try {
            Log.e("szp","获取路径下的文件转换");
            RandomAccessFile randomAccessFile = new RandomAccessFile(musicFile, "rw");
            Log.e("szp","randomAccessFile"+randomAccessFile);
            byte[] buffer = new byte[128];
            randomAccessFile.seek(randomAccessFile.length() - 128);
            randomAccessFile.read(buffer);
            if (buffer.length == 128) {
                Log.e("szp","字符编码");
                v1Entity = new MusicInfoV1Entity();
                String tag = new String(buffer, 0, 3);
                //字符编码解析工具，需要引入jar文件
                UniversalDetector detector = new UniversalDetector(null);
                detector.handleData(buffer, 0, buffer.length);
                detector.dataEnd();
                String charset = detector.getDetectedCharset();
                detector.reset();
// 只有前三个字节是TAG才处理后面的字节
                if (tag.equalsIgnoreCase("TAG")) {
                    Log.e("szp","显示信息");
// 歌曲名
                    String songName = new String(buffer, 3, 30, charset).trim();
// 艺术家
                    String artist = new String(buffer, 33, 30, charset).trim();
// 所属唱片
                    String album = new String(buffer, 63, 30, charset).trim();
// 发行年
                    String year = new String(buffer, 93, 4, charset).trim();
// 备注
                    String comment = new String(buffer, 97, 28, charset).trim();

                    v1Entity.setTitle(songName);
                    v1Entity.setArtist(artist);
                    v1Entity.setAlbum(album);
                    v1Entity.setYear(year);
                    v1Entity.setComment(comment);
                    Log.e("szp","歌曲名:" + songName);
                    Log.e("szp","艺术家:" + artist);
                    Log.e("szp","所属唱片:" + album);
                    Log.e("szp","发行年:" + year);
                    Log.e("szp","备注:" + comment);
                    return v1Entity;
                } else {
                    Log.e("szp","无效的歌曲信息...");
                    return null;
                }
            }
            Log.e("szp","地址有问题");


        } catch (FileNotFoundException e) {
            Log.e("szp","异常1");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("szp","异常2");
            e.printStackTrace();
        }
        Log.e("szp","null");
        return null;
    }


    /**
     * 写入mp3的ID3V1文件信息
     *
     * @param path
     * @param v1Entity
     */
    public static void setMusicInfoV1(String path, MusicInfoV1Entity v1Entity) {
        try {
            byte[] bufferAll = new byte[128];
            byte[] buffTag;
            byte[] buffSoundName = new byte[30];
            byte[] buffArtist = new byte[30];
            byte[] buffAlbum = new byte[30];
            byte[] buffYear = new byte[4];
            byte[] buffComment = new byte[28];
            byte[] buffFoot;

            buffTag = "TAG".getBytes();
            byte[] cache;
            if (v1Entity.getTitle() != null) {
                cache = v1Entity.getTitle().getBytes("GBK");
                System.arraycopy(cache, 0, buffSoundName, 0, cache.length);
            }

            if (v1Entity.getArtist() != null) {
                cache = v1Entity.getArtist().getBytes("GBK");
                System.arraycopy(cache, 0, buffArtist, 0, cache.length);
            }

            if (v1Entity.getAlbum() != null) {
                try {
                    cache = v1Entity.getAlbum().getBytes("GBK");
                    System.arraycopy(cache, 0, buffAlbum, 0, cache.length);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            if (v1Entity.getYear() != null) {
                cache = v1Entity.getYear().getBytes("GBK");
                System.arraycopy(cache, 0, buffYear, 0, cache.length);
            }

            if (v1Entity.getComment() != null) {
                cache = v1Entity.getComment().getBytes("GBK");
                int num = 28;
                if (cache.length <= num) {
                    num = cache.length;
                }
                System.arraycopy(cache, 0, buffComment, 0, num);
            }
            buffFoot = "111".getBytes();

            System.arraycopy(buffTag, 0, bufferAll, 0, 3);
            System.arraycopy(buffSoundName, 0, bufferAll, 3, 30);
            System.arraycopy(buffArtist, 0, bufferAll, 33, 30);
            System.arraycopy(buffAlbum, 0, bufferAll, 63, 30);
            System.arraycopy(buffYear, 0, bufferAll, 93, 4);
            System.arraycopy(buffComment, 0, bufferAll, 97, 28);
            System.arraycopy(buffFoot, 0, bufferAll, 125, 3);


            RandomAccessFile randomAccessFile = new RandomAccessFile(new File(path), "rw");

            long len = randomAccessFile.length();
            if (getMusicInfoV1(path) != null) {
                //有v1了，需要把后面的128删掉
                len = randomAccessFile.length() - 128;
            }
            randomAccessFile.seek(len);
            randomAccessFile.write(bufferAll, 0, bufferAll.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
