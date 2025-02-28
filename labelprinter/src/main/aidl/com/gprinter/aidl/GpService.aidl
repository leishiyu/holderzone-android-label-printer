package com.gprinter.aidl;


/**
 * GpService aidl 描述文件
 *
 * PrinterId:取值为 0、1、2
 *
 * 说明:打印机的序号，Gplink 插件可以同时连接 3 台打印机：Printer001、Printer002、Printer003，依次编号为 0 、1 、2
 *
 * ERROR_CODE（错误码）定义如下
 * ERROR_CODE.SUCCESS //正常
 * FAILED //失败
 * TIMEOUT // 超时
 * INVALID_DEVICE_PARAMETERS //无效的参数
 * DEVICE_ALREADY_OPEN //端口已经打开
 * INVALID_PORT_NUMBER //无效的端口号
 * INVALID_IP_ADDRESS //无效的 ip 地址
 * INVALID_CALLBACK_OBJECT //无效的回调
 * BLUETOOTH_IS_NOT_SUPPORT //设备不支持蓝牙
 * OPEN_BLUETOOTH //请打开蓝牙
 * PORT_IS_NOT_OPEN //端口未打开
 * INVALID_BLUETOOTH_ADDRESS //无效的蓝牙地址
 * PORT_IS_DISCONNECT //端口连接断开
 *
 * GpDevice.STATE（连接状态值）定义如下
 * GpDevice.STATE_NONE = 0; //连接断开
 * GpDevice.STATE_LISTEN = 1; //监听状态
 * GpDevice.STATE_CONNECTING = 2; //正在连接
 * GpDevice.STATE_CONNECTED = 3; //已连接
 * GpDevice.STATE_INVALID_PRINTER = 4; //无效的打印机
 * GpDevice.STATE_VALID_PRINTER = 5; //有效的打印机
 *
 * GpCom（设备状态值）定义如下
 * GpCom.STATE_NO_ERR = 0; //正常
 * STATE_OFFLINE = 0x1; //脱机
 * STATE_PAPER_ERR = 0x2;//缺纸
 * STATE_COVER_OPEN = 0x4;//开盖
 * STATE_ERR_OCCURS = 0x8;//过热错误
 *
 * aidl文件只能使用Java语言来定义，不能使用Kotlin
 */
interface GpService{

    /**
     * 打开端口,连接过程为异步的，可以通过注册"action.connect.status"广播来接收端口连接的状态
     *
     * GpDevice.STATE_NONE = 0; //连接断开
     * GpDevice.STATE_LISTEN = 1; //监听状态
     * GpDevice.STATE_CONNECTING = 2; //正在连接
     * GpDevice.STATE_CONNECTED = 3; //已连接
     * GpDevice.STATE_INVALID_PRINTER = 4; //无效的打印机
     * GpDevice.STATE_VALID_PRINTER = 5; //有效的打印机
     *
     * @param PrinterId 打印机序号
     * @param PortType 端口类型 PortParameters.USB = 2；PortParameters.ETHERNET = 3；PortParameters.BLUETOOTH = 4
     * @param DeviceName 设备名 如果端口为 USB，则为 USB 设备的 DeviceName；如果端口为 ETHERNET，则为 IP 地址；如果端口为 BLUETOOTH，则为蓝牙 Mac 地址
     * @param PortNumber 端口号 如果端口为 USB ,则为 0；如果端口为 ETHERNET，则为端口号，打印机一般为 9100 端口；如果端口为 BLUETOOTH,则为 0
     * @return 错误值，如果端口已连接返回 ERROR_CODE.DEVICE_ALREADY_OPEN
     */
    int openPort(int PrinterId, int PortType, String DeviceName, int PortNumber);

    /**
     * 关闭端口
     *
     * @param PrinterId 打印机的序号
     */
    void closePort(int PrinterId);

    /**
     * 获取打印机连接状态
     *
     * GpDevice.STATE_NONE = 0; //连接断开
     * GpDevice.STATE_LISTEN = 1; //监听状态
     * GpDevice.STATE_CONNECTING = 2; //正在连接
     * GpDevice.STATE_CONNECTED = 3; //已连接
     * GpDevice.STATE_INVALID_PRINTER = 4; //无效打印机
     * GpDevice.STATE_VALID_PRINTER = 5; //有效打印机
     *
     * @param PrinterId 打印机的序号
     * @return 状态值
     */
    int getPrinterConnectStatus(int PrinterId);

    /**
     * 打印测试页
     *
     * @param PrinterId 打印机的序号
     * @return 错误值
     */
    int printeTestPage(int PrinterId);

    /**
     * 查询打印机状态，以广播的形式返回状态
     *
     * 广播的 action -> GpCom.ACTION_DEVICE_REAL_STATUS
     * 广播的 extra ->  int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
     *                 int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
     * 广播的 extra status定义：
     * GpCom.STATE_NO_ERR = 0; //正常
     * STATE_OFFLINE = 0x1; //脱机
     * STATE_PAPER_ERR = 0x2;//缺纸
     * STATE_COVER_OPEN = 0x4;//开盖
     * STATE_ERR_OCCURS = 0x8;//过热错误
     *
     * @param PrinterId 打印机的序号
     * @param Timesout 接收超时时间ms：因为蓝牙返回数据会有延时，所以此时间需设置为500-1000ms，根据设备和环境而定，USB和WIFI返回数据较快,一般设置为100-500ms左右
     * @param requestCode 请求码，用于
     */
    void queryPrinterStatus(int PrinterId, int Timesout, int requestCode);

    /**
     * 查询打印机指令类型
     *
     * 打印机指令类型：
     * GpCom.ESC_COMMAND = 0;
     * GpCom.LABEL_COMMAND = 1;
     *
     * 默认为0，所以为0的时候可能代表是Esc命令，也可能代表尚未被正确初始化
     *
     * @param PrinterId 打印机的序号
     * @return 打印机的指令类型
     */
    int getPrinterCommandType(int PrinterId);

    /**
     * 发送 ESC 指令，发送此命令时，确保打印机处于票据模式，否则发送无效
     *
     * @param PrinterId 打印机的序号
     * @param b64 发送的票据数据
     * @return 错误值
     */
    int sendEscCommand(int PrinterId, String b64);

    /**
     * 发送 TSC 指令，发送此命令时，确保打印机处于标签模式，否则发送无 效
     *
     * @param PrinterId 打印机的序号
     * @param b64 发送的标签数据
     * @return 错误值
     */
    int sendLabelCommand(int PrinterId, String  b64);

    /**
     * 参与用户体验
     *
     * @param userExperience 是否参与
     */
    void isUserExperience(boolean userExperience);

    /**
     * 获取客户端id
     *
     * @return 客户端id
     */
    String getClientID();

    /**
     * 设置服务端ip
     *
     * @param ip 服务端ip
     * @param port 服务端port
     * @return
     */
    int setServerIP(String ip, int port);

    /**
     * 设置打印机指令类型
     *
     * 打印机指令类型：
     * ESC_COMMAND = 0;
     * LABEL_COMMAND = 1;
     *
     * @param printerId 打印机的序号
     * @param commandType 打印机指令类型
     * @param response 是否响应
     */
    void setCommandType(int printerId, int commandType, boolean response);
}