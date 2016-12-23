package com.tonghaoyao.mobilepalyer2.domain;

/**
 * Created by dell1 on 2016-12-18 .
 * 作者: 童浩瑶 on 10:23
 * QQ号: 1339170870
 * 作用: 歌词类
 * 例如:
 * [01:21.35]我在这里寻找
 */
public class Lyric {
    /**
     * 歌词内容
     */
    private String content;

    /**
     * 时间戳
     */
    private long timePoint;

    /**
     * 休眠时间或者高亮显示时间
     */
    private long sleeptime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSleeptime() {
        return sleeptime;
    }

    public void setSleeptime(long sleeptime) {
        this.sleeptime = sleeptime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleeptime=" + sleeptime +
                '}';
    }
}
