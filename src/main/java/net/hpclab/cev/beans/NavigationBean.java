package net.hpclab.cev.beans;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named(value = "navigationBean")
@SessionScoped
public class NavigationBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 2801082271772326617L;
	private String navigablePage;

	public NavigationBean() {
	}

	public boolean isPageReady() {
		return navigablePage != null && !navigablePage.isEmpty();
	}

	public String getNavigablePage() {
		return navigablePage;
	}

	public void setNavigablePage(String navigablePage) {
		this.navigablePage = navigablePage;
	}

}
