package cn.lx.security.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * cn.lx.security.utils
 *
 * @Author Administrator
 * @date 15:07
 */
@Slf4j
public class JwtUtil {

    /**
     * 创建令牌
     * @param claims
     * @return
     */
    public static String createJwt(Map<String, Object> claims){
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
                    //设置令牌过期时间30分钟
                    .setExpiration(new Date(System.currentTimeMillis()+1000*60*30))
                    //为令牌设置额外的信息
                    .addClaims(claims)
                    //指定加密类型为rsa
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    //得到令牌
                    .compact();
            log.info("创建令牌成功："+jws);
          return jws;
        } catch (Exception e) {
            throw new RuntimeException("创建令牌失败");
        }
    }


    /**
     * 解析令牌
     * @param compactJws
     * @return
     */
    public static String decodeJwt(String compactJws){
        //获取公钥
        String pubKey = KeyUtil.readKey("publicKey.txt");
        //将string类型的私钥转换成PublicKey，jwt只能接受PublicKey的公钥
        KeyFactory keyFactory;
        try {
            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(
                    new BASE64Decoder().decodeBuffer(pubKey));

            keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(bobPubKeySpec);

            Claims body = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(compactJws).getBody();

            String jwtString = JSON.toJSONString(body);

            //OK, we can trust this JWT
            log.info("解析令牌成功："+jwtString);
          return jwtString;
        } catch (Exception e) {
            throw new RuntimeException("解析令牌失败");
        }
    }


    /**
     * 解析令牌并获取用户名和权限
     * @param compactJws
     * @return String[0]用户名
     * String[1]权限
     */
    public static String[] extractAndDecodeJwt(String compactJws){
        //获取令牌的内容
        String decodeJwt = decodeJwt(compactJws);
        JSONObject jsonObject = JSON.parseObject(decodeJwt);
        String username = jsonObject.getString("username");
        String authorities = jsonObject.getString("authorities");
        return new String[] { username, authorities };
    }

}
