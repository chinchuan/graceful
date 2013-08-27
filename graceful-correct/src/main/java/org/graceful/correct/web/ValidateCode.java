package org.graceful.correct.web;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ValidateCode {
	
	//验证码图片的宽度。
	private int width=60; 
	//验证码图片的高度。
	private int height=20; 
	//验证码字符个数
	private int codeCount=4; 

	//字体高度
	private int fontHeight; 
	
	char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
			'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' }; 
	
	
	public String  getCode(OutputStream out,Integer width,Integer height) throws IOException{
		if(width!=null  && width!=0)
			this.width = width;
		if(height!=null && height!=0)
			this.height = height;
		//定义图像buffer
		BufferedImage buffImg = new BufferedImage(this.width, this.height,BufferedImage.TYPE_INT_RGB); 
		Graphics2D g = buffImg.createGraphics(); 

		//创建一个随机数生成器类
		Random random = new Random(); 

		//将图像填充为白色
		g.setColor(Color.WHITE); 
		g.fillRect(0, 0, width, height); 
		
		//画边框。
		g.setColor(Color.BLACK); 
		g.drawRect(0, 0, width - 1, height - 1); 

		//创建字体，字体的大小应该根据图片的高度来定。
		this.fontHeight = this.height * 14 / 20;
		Font font = new Font("Fixedsys", Font.PLAIN, fontHeight); 
		//设置字体。
		g.setFont(font); 
		FontMetrics fm = g.getFontMetrics();
		int asc = fm.getAscent();
		int desc = fm.getDescent();

		//randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
		StringBuffer randomCode = new StringBuffer(codeCount); 

		//随机产生codeCount数字的验证码。
		for (int i = 0; i < codeCount; i++) {
			//得到随机产生的验证码数字。
			String strRand = String.valueOf(codeSequence[random.nextInt(36)]); 
			//将产生的四个随机数组合在一起。
			randomCode.append(strRand); 
		}
		
		// 字符宽
		int fontWidth = fm.stringWidth(randomCode.toString());
		int avg = (this.width - fontWidth) / randomCode.length();
		int xPos = avg / 2;
		int yPos = 0;
		
		// 绘制char,每个字符颜色随机
		int red = 0, green = 0, blue = 0; 
		for (int i = 0; i < codeCount; i++) {
			String c = randomCode.substring(i, i + 1);
			
			//产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
			red = random.nextInt(255); 
			green = random.nextInt(255); 
			blue = random.nextInt(255); 
	
			//用随机产生的颜色将验证码绘制到图像中。
			g.setColor(new Color(red, green, blue)); 
		 
			// 垂直方向上随机
			yPos = asc + random.nextInt((this.height - desc) - asc);
			g.drawString(c, xPos, yPos);
			xPos += avg + fm.stringWidth(c);
		}

		//随机产生6条干扰线，使图象中的认证码不易被其它程序探测到。
		g.setColor(Color.BLACK); 
		for(int i = 0; i < 6; i++){
			int x = random.nextInt(width); 
			int y = random.nextInt(height); 
			int xl = random.nextInt(12); 
			int yl = random.nextInt(12); 
			g.drawLine(x, y, x + xl, y + yl); 
		}
		
		ImageIO.write(buffImg, "jpeg", out); 
		out.close();
		return randomCode.toString();
	}
	
	public void generate(HttpServletRequest request,HttpServletResponse response,Integer width,Integer height) throws IOException{
		OutputStream out = response.getOutputStream(); 
		// 禁止图像缓存。
		response.setHeader("Pragma", "no-cache"); 
		response.setHeader("Cache-Control", "no-cache"); 
		response.setDateHeader("Expires", 0); 
		response.setContentType("image/jpeg"); 
		
		HttpSession session = request.getSession();
		String  randomCode = getCode(out,width,height);
		session.setAttribute("validateCode", randomCode);
	}
	
	public boolean validator(String code,HttpServletRequest request){
		HttpSession session = request.getSession();
		String validateCode = (String)session.getAttribute("validateCode");
		if(validateCode == null)
			return false;
		if(!validateCode.equals(code))
			return false;
		return true;
	}
	
}
