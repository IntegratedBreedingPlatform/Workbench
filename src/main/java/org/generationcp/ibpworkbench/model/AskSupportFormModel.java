package org.generationcp.ibpworkbench.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.web.multipart.MultipartFile;

public class AskSupportFormModel {

	private MultipartFile file;
	private String name;
	private String email;
	private String summary;
	private String description;
	private String requestCategory;

	public static final String[] CATEGORIES=  new String[]{
		"support.category.analysis",
		"support.category.breeder.queries",
		"support.category.breeding.processes",
		"support.category.browser.tools",
		"support.category.crossing.manager",
		"support.category.data.import.tool",
		"support.category.data.management",
		"support.category.documentation",
		"support.category.gdms",
		"support.category.germplasm.import",
		"support.category.label.printing",
		"support.category.list.manager",
		"support.category.nursery.manager",
		"support.category.ontology.manager",
		"support.category.trial.manager",
		"support.category.workbench.program.admin"
	};

	public AskSupportFormModel() {
	}

	public AskSupportFormModel(MultipartFile file, String name, String email, String summary, String description, String requestCategory) {
		this.file = file;
		this.name = name;
		this.email = email;
		this.summary = summary;
		this.description = description;
		this.requestCategory = requestCategory;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRequestCategory() {
		return requestCategory;
	}

	public void setRequestCategory(String requestCategory) {
		this.requestCategory = requestCategory;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof AskSupportFormModel)) {
			return false;
		}

		AskSupportFormModel that = (AskSupportFormModel) o;

		return new EqualsBuilder().append(file, that.file).append(name, that.name).append(email, that.email).append(summary, that.summary)
				.append(description, that.description).append(requestCategory, that.requestCategory).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(file).append(name).append(email).append(summary).append(description)
				.append(requestCategory).toHashCode();
	}

	@Override
	public String toString() {
		return "AskSupportFormModel{" +
				"file=" + file +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", summary='" + summary + '\'' +
				", description='" + description + '\'' +
				", requestCategory='" + requestCategory + '\'' +
				'}';
	}
}
