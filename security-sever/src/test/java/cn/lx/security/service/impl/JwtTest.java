package cn.lx.security.service.impl;

import cn.lx.security.util.KeyUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.misc.BASE64Decoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * cn.lx.security.service.impl
 *
 * @Author Administrator
 * @date 11:12
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class JwtTest {

    /**
     * 创建令牌
     */
    @Test
    public void createJwt(){
        //获取私钥
        String priKey = KeyUtil.readKey("privateKey.txt");
        //将string类型的私钥转换成PrivateKey，jwt只能接受PrivateKey的私钥
        PKCS8EncodedKeySpec priPKCS8 = null;
        try {
            priPKCS8 = new PKCS8EncodedKeySpec(new BASE64Decoder().decodeBuffer(priKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyf.generatePrivate(priPKCS8);
            //创建令牌
            String jws = Jwts.builder()

                    .setSubject("Bob")

                    //指定加密类型为rsa
                    .signWith(privateKey, SignatureAlgorithm.RS256)

                    .compact();
            System.out.println(jws);

        } catch (Exception e) {
           throw new RuntimeException("创建令牌失败");
        }


    }


    /**
     * 解析令牌
     */
    @Test
    public void decodeJwt(){
        //获取公钥
        String pubKey = KeyUtil.readKey("publicKey.txt");
        //将string类型的私钥转换成PublicKey，jwt只能接受PublicKey的公钥
        KeyFactory keyFactory;
        try {
            String compactJws="eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJCb2IifQ.mFL1I02bTomAl0sPUcP0twDFbJjrnUw0vWMqnS3z9EodVEA8NG8KxKw0cVOT6-gdmxhfxrj1mfj0BlijA70KhWMM2qtsl8GviVHgGUaT0Yl98r81oIKa61YPGQ4ZWr6J7oyam5V62sBzStPoTQUoltebXhbnjmvUxuNdxMpb09cGF2qiZne3NVpFQtMcfdi0X3nYZhLRRk9VP0WRDGUPrO2aPQxnp_chrQnLvbx2cXi7hCYks40Gg4QGD8rn4vGkbpt-hEUTTKZc00UyBsc0Pi3Gz3CFpSjHyJcEOuBdjiB4zo9icw_w_0ys0mmCtrG1uoPmOR5VVJYxFbxNLVBhJw";
            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(
                    new BASE64Decoder().decodeBuffer(pubKey));

            keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(bobPubKeySpec);
            String jwtString = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(compactJws).toString();

            //OK, we can trust this JWT
            System.out.println("验证成功:"+jwtString);
        } catch (Exception e) {
            throw new RuntimeException("解析令牌失败");
        }
    }

}
