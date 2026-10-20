package cl.mascotadatos.recovery

import org.junit.Assert.*
import org.junit.Test

class EmailValidatorTest {
    @Test fun allowsOrdinaryAndTaggedAddresses() {
        listOf("nombre@correo.cl", " Nombre.Apellido+mascotas@example.com ",
            "a@sub.dominio.cl").forEach { assertTrue(it, EmailValidator.isValid(it)) }
    }
    @Test fun rejectsMalformedOrOversizedAddresses() {
        listOf("", " ", "nombre", "a@@dominio.cl", "a@dominio", ".a@correo.cl",
            "a..b@correo.cl", "a@-correo.cl", "a@correo..cl", "a b@correo.cl",
            "a@correo.123", "a".repeat(65) + "@correo.cl")
            .forEach { assertFalse(it, EmailValidator.isValid(it)) }
    }
}
