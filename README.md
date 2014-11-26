SMS-China
=========

中国移动CMPP、联通SGIP、电信SMGP三网合一企业短信网关

java sample code:


    public class CMPPTester {
        private static Logger log = LogManager.getLogger(CMPPTester.class);
        private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

        public static void main(String[] args) {
            CMPPConnection conn = new CMPPConnection();
            conn.setSourceAddr("123456");
            conn.setPassword("aaa001");
            conn.setVersion((byte) 0);
            conn.setAutoReconnect(true);
            conn.setSendInterval(200);

            conn.connect("127.0.0.1", 7890);

            if(conn.isConnected()){

                Session session = conn.getSession();

                String[] phones = new String[] { "1316264XXXX" };

                long startTime = System.currentTimeMillis();
                try {
                    for(int i = 0; i < phones.length * 10; i++) {
                       String content = String.format("第%d条:电信cmpp测试X(%s)", i+1, format.format(new Date()));
                        session.submit(content, "1065902100612", phones[i/10]);
                    }
                }
                finally {
                    log.info(String.format("total:%d",System.currentTimeMillis()-startTime));
                }
            }
        }
    }

