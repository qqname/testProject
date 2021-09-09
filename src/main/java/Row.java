public class Row {
    private int id;
    private String name;
    private String profession;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String asString (){
        return String.format("(%s,'%s','%s')",this.id,this.name,this.profession);
    }
}
