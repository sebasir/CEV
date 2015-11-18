package net.hpclab.utilities;

import java.util.HashMap;

public class AuthorPivot {

    private Integer idAuthor;
    private HashMap<Integer, Integer> idTypes;
    private String authorName;

    public AuthorPivot() {
	   idTypes = new HashMap<Integer, Integer>();
    }

    public AuthorPivot(Integer idAuthor) {
	   this.idAuthor = idAuthor;
    }

    public AuthorPivot(Integer idAuthor, String authorName) {
	   this.idAuthor = idAuthor;
	   this.authorName = authorName;
    }

    public Integer getIdAuthor() {
	   return idAuthor;
    }

    public void setIdAuthor(Integer idAuthor) {
	   this.idAuthor = idAuthor;
    }

    public HashMap<Integer, Integer> getIdTypes() {
	   if (idTypes == null) {
		  idTypes = new HashMap<Integer, Integer>();
	   }
	   return idTypes;
    }

    public void setIdTypes(HashMap<Integer, Integer> idTypes) {
	   this.idTypes = idTypes;
    }

    public String getAuthorName() {
	   return authorName;
    }

    public void setAuthorName(String authorName) {
	   this.authorName = authorName;
    }

    @Override
    public boolean equals(Object other) {
	   if (other instanceof AuthorPivot) {
		  if (((AuthorPivot) other).idAuthor.equals(this.idAuthor)) {
			 return true;
		  }
	   }
	   return false;
    }

    @Override
    public int hashCode() {
	   int hash = 5;
	   hash = 43 * hash + (this.idAuthor != null ? this.idAuthor.hashCode() : 0);
	   return hash;
    }
}
