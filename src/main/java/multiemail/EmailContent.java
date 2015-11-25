package multiemail;

public class EmailContent {

	private String subject;
	private String htmlText;
	private String accessibleText;

	

	public EmailContent(String subject, String htmlText, String accessibleText) {
		super();
		this.subject = subject;
		this.htmlText = htmlText;
		this.accessibleText = accessibleText;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getHtmlText() {
		return htmlText;
	}

	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
	}

	public String getAccessibleText() {
		return accessibleText;
	}

	public void setAccessibleText(String accessibleText) {
		this.accessibleText = accessibleText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessibleText == null) ? 0 : accessibleText.hashCode());
		result = prime * result + ((htmlText == null) ? 0 : htmlText.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailContent other = (EmailContent) obj;
		if (accessibleText == null) {
			if (other.accessibleText != null)
				return false;
		} else if (!accessibleText.equals(other.accessibleText))
			return false;
		if (htmlText == null) {
			if (other.htmlText != null)
				return false;
		} else if (!htmlText.equals(other.htmlText))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmailContent [subject=" + subject + ", htmlText=" + htmlText + ", accessibleText=" + accessibleText
				+ "]";
	}

}
