

#ifndef AUTO_FILE_H
#define AUTO_FILE_H

#include <string>

class File {
public:
    //写入文件
    static void writeFile(const std::string &filename, const std::string &fileInfo);
    //读取文件
    static std::string readFile(const std::string &filename);
    //修剪日志文件
    static void trimLogFile(const std::string &filename, size_t maxLines);
    //格式化时间
    static std::string formatTime();
    //写入日志
    static void writeLog(const std::string &fileInfo);
    //写入数据
    static void writeData(const std::string &fileInfo);
    //文件是否存在
    static bool fileExists(const std::string &string);
    //创建目录
    static void createDir(const std::string& path);
    //目录是否存在
    static bool directoryExists(const std::string& path);

};


#endif //AUTO_FILE_H
