package com.example.concurrency.features.synchronizedEx;

/**
 * 描述:
 * 保护没有关联关系的多个资源
 * 银行账户密码和余额没有关联关系
 * @author zed
 * @since 2019-06-13 2:12 PM
 */
public class SynchronizedNoConnection {

    class Account {
        /**
         * 锁：保护账户余额
         */
        private final Object balLock = new Object();
        /**
         * 账户余额
         */
        private Integer balance;
        /**
         * 锁：保护账户密码
         */
        private final Object pwLock = new Object();
        /**
         *  账户密码
         */
        private String password;

        /**
         * 取款
         * @param amt amount
         */
        void withdraw(Integer amt) {
            synchronized(balLock) {
                if (this.balance > amt){
                    this.balance -= amt;
                }
            }
        }

        /**
         * 查看余额
         * @return balance
         */
        Integer getBalance() {
            synchronized(balLock) {
                return balance;
            }
        }

        /**
         * 更改密码
         * @param pw password
         */
        void updatePassword(String pw){
            synchronized(pwLock) {
                this.password = pw;
            }
        }
        /**
         *  查看密码
         * @return  password
         */
        String getPassword() {
            synchronized(pwLock) {
                return password;
            }
        }
    }
}

