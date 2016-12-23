// IMusicPlayerService.aidl
package com.tonghaoyao.mobilepalyer2;

// Declare any non-default types here with import statements

interface IMusicPlayerService {

                     /* 根据位置打开对应的音频文件
                     * @param position
                     */
                    void openAudio (int position);

                    /**
                     * 播放音乐
                     */
                    void start();

                    /**
                     * 暂停音乐
                     */
                    void pause();

                    /**
                     *  停止音乐
                     */
                    void stop();
                    /**
                     * 得到当前的播放进度
                     * @return
                     */
                    int getCurrentPosition ();

                    /**
                     * 得到当前音频的总时长
                     * @return
                     */
                    int getDuration ();

                    /**
                     * 得到艺术家
                     * @return
                     */
                    String getArtist ();

                    /**
                     *  得到歌曲的名字
                     * @return
                     */
                    String getName();

                    /**
                     *  得到播放的路径
                     * @return
                     */
                    String getAudioPath ();

                    /**
                     * 播放下一个视频
                     */
                    void next();

                    /**
                     * 播放上一个视频
                     */
                    void pre();


                    /**
                     * 设置播放模式
                     * @param playMode
                     */
                    void setPlayMode(int playMode);

                    /**
                     * 得到播放模式
                     */
                    int getPlayMode();

                    /**
                    * 是否正在播放
                    */
                    boolean isPlaying();

                    //拖动进度条
                    void seekTo(int position);

                    int getAudioSessionId();

}
