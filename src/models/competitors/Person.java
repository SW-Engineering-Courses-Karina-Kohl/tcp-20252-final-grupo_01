package models.competitors;

import java.time.LocalDate;

public final class Person extends Competitor {

    private String cpf;
    private String phone;
    private LocalDate birthDate;

    private Person(String name) {
        super(name);
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
