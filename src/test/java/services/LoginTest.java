package services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.hpclab.cev.beans.UtilsBean;
import net.hpclab.cev.enums.AuthenticateEnum;
import net.hpclab.cev.services.LoginService;
import net.hpclab.cev.services.Util;

@RunWith(Arquillian.class)
public class LoginTest {

	private LoginService loginService;

	@Deployment
	public static JavaArchive createDeployment() {
		return ShrinkWrap.create(JavaArchive.class).addClasses(LoginService.class, UtilsBean.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Test
	public void test() {
		loginService = LoginService.getInstance();
		try {
			AuthenticateEnum en = loginService.login(null, "admin", Util.encrypt("Admin.Cev.123"), "@ucentral.edu.co");
			assertEquals(en, AuthenticateEnum.LOGIN_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			fail("LoginTest.test error: " + e.getMessage());
		}
	}

}
