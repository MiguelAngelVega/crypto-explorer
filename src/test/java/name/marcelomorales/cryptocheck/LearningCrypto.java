/*
 *  Copyright 2009 Marcelo Morales.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package name.marcelomorales.cryptocheck;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Marcelo Morales
 */
public class LearningCrypto {

    /**
     * Sólo quiero ver los diferentes modos de encriptación de AES.
     * @throws Exception
     */
    @Test
    public void checkAes() throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/ISO10126Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[]
                {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}, "AES"));
        byte[] bytes = cipher.doFinal(new byte[]{3});
        System.out.println(new String(Hex.encode(bytes)));

        cipher = Cipher.getInstance("AES/ECB/ISO10126Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[]
                {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24}, "AES"));
        bytes = cipher.doFinal(new byte[]{3});
        System.out.println(new String(Hex.encode(bytes)));

        cipher = Cipher.getInstance("AES/ECB/ISO10126Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[]
                {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
                        26, 27, 28, 29, 30, 31, 32}, "AES"));
        bytes = cipher.doFinal(new byte[]{3});
        System.out.println(new String(Hex.encode(bytes)));

        String[] modes = new String[]{
                "ECB",
                "CBC", // iv = block size
                "OFB", // obf??
                "CFB", // igual que OFB, con numeros
                "PGP",
                "PGPCFBwithIV",
                "OpenPGPCFB",
                "SIC",
                "CTR",
                "GOFB",
                "CTS",
                "CCM",
                "EAX",
                "GCM"
        };

        String[] paddings = new String[]{
                "NOPADDING",
                "WITHCTS",
                "PKCS5PADDING",
                "PKCS7PADDING",
                "ZEROBYTEPADDING",
                "ISO10126PADDING",
                "X9.23PADDING",
                "ISO7816-4PADDING",
                "ISO9797-1PADDING",
                "TBCPADDING"
        };

        BouncyCastleProvider provider = new BouncyCastleProvider();

        for (String mode : modes) {
            for (String padding : paddings) {
                String transformation = "AES/" + mode + "/" + padding;
                try {
                    cipher = Cipher.getInstance(transformation, provider);
                    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[]
                            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
                                    26, 27, 28, 29, 30, 31, 32}, "AES"),
                            new IvParameterSpec(new byte[]
                                    {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}));
                    bytes = cipher.doFinal(new byte[]
                            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16});
                    System.out.println(new String(Hex.encode(bytes)));
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    System.out.println(e.getMessage() + " " + (cause != null ? cause.getMessage() : "") + " con " + transformation);
                }
            }
        }


    }
}
