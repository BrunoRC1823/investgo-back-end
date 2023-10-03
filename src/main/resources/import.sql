INSERT INTO roles (id,codigo,rol) VALUES(1,'ROLE_ADMIN','Administrador')
INSERT INTO roles (id,codigo,rol) VALUES(2,'ROLE_USER','Usuario')
INSERT INTO riesgos (id,rango,descripcion) VALUES(1,'A','El reisgo de invertir en esta empresa es MUY bajo')
INSERT INTO riesgos (id,rango,descripcion) VALUES(2,'B','El reisgo de invertir en esta empresa es bajo')
INSERT INTO riesgos (id,rango,descripcion) VALUES(3,'C','El reisgo de invertir en esta empresa es moderado')
INSERT INTO riesgos (id,rango,descripcion) VALUES(4,'D','El reisgo de invertir en esta empresa es alto')
INSERT INTO riesgos (id,rango,descripcion) VALUES(5,'E','Esta empresa no cumple con los requisitos y pronto sera removida')
INSERT INTO empresas (id,codigo,nombre_representante_legal,apellido_representante_legal,nombre_empresa,ruc,razon_social,fecha_inicio_actv,direccion,telefono,correo,nro_cuenta_bancaria,sector,enable,fecha,riesgo_id) VALUES('32e4cc12-ecd2-4b82-bf5f-b52631bef5ae','EMP000001','Bruno','Rios','Brunos','20552103816','BRUNO S.A.C',NOW(),'mi casa','934129008','Bruno@hotmail.com','12345678910','Pesquero',1,NOW(),1)
INSERT INTO facturas (id,codigo,descripcion,monto,fecha_emision,enable,fecha,empresa_id) VALUES('0f3788ac-b9d5-4f2f-b354-ed0e3b68b7fa','FAC000001','Compras de materia prima',5000.00,NOW(),0,NOW(),'32e4cc12-ecd2-4b82-bf5f-b52631bef5ae')
INSERT INTO facturas (id,codigo,descripcion,monto,fecha_emision,enable,fecha,empresa_id) VALUES('ed55b3e2-c690-44b1-9c34-f1963ed3e718','FAC000002','Compras de productos de oficina',7000.00,NOW(),0,NOW(),'32e4cc12-ecd2-4b82-bf5f-b52631bef5ae')
INSERT INTO oportunidad_inversiones (id,codigo,rendimiento,tir,en_proceso,monto,monto_recaudado,fecha_caducidad,fecha_pago,enable,fecha,empresa_id) VALUES('a576eb17-274b-4f7b-b977-24e25e322215','OPI000001',0.2,0.3,0,10000,0,NOW(),NOW(),1,NOW(),'32e4cc12-ecd2-4b82-bf5f-b52631bef5ae')
INSERT INTO oportunidad_inversion_has_facturas (id,oportunidad_inversion_id,factura_id) VALUES(1,'a576eb17-274b-4f7b-b977-24e25e322215','0f3788ac-b9d5-4f2f-b354-ed0e3b68b7fa')
INSERT INTO oportunidad_inversion_has_facturas (id,oportunidad_inversion_id,factura_id) VALUES(2,'a576eb17-274b-4f7b-b977-24e25e322215','ed55b3e2-c690-44b1-9c34-f1963ed3e718')
INSERT INTO bancos (id,nombre) VALUES(1,'BBVA')
INSERT INTO bancos (id,nombre) VALUES(2,'Scotiabank')
INSERT INTO bancos (id,nombre) VALUES(3,'BanBif')
INSERT INTO bancos (id,nombre) VALUES(4,'InterBank')
INSERT INTO monedas (id,nombre,valor) VALUES(1,'Nuevo Sol','PEN')
INSERT INTO monedas (id,nombre,valor) VALUES(2,'Dolar','USD')
INSERT INTO monedas (id,nombre,valor) VALUES(3,'Euro','EUR')
INSERT INTO usuarios (id,codigo,apellido_pa,apellido_ma,nombre,correo,password,telefono,username,dni,enable,fecha,foto,rol_id) VALUES ('cd7ae398-d090-4127-bb29-02a90bfe8828' ,'USU000001','Rios','Cosser','Bruno','bruno@hotmail.com','$2a$10$PLX7adAgiQo1sful9GxofuZfbBjEIgYgNGejUokemtRFEEGQ3i4xS','934129008','Admin',72729345,1,NOW(),'',1);
INSERT INTO usuarios (id,codigo,apellido_pa,apellido_ma,nombre,correo,password,telefono,username,dni,enable,fecha,foto,rol_id) VALUES ('0173a9dc-1a01-4e70-aa21-a6827e1625a4','USU000002','Doe','Doe','Jhon','jhon@hotmail.com','$2a$10$PLX7adAgiQo1sful9GxofuZfbBjEIgYgNGejUokemtRFEEGQ3i4xS','934129008','JhonDoe',72729346,1,NOW(),'',2);
INSERT INTO carteras (id,saldo,usuario_id) VALUES('358487ed-ba26-4d61-a9bb-5a3949d89393',100000,'cd7ae398-d090-4127-bb29-02a90bfe8828')
INSERT INTO carteras (id,saldo,usuario_id) VALUES('65c8b892-88fc-487b-98cf-3168f5de28ac',1000,'0173a9dc-1a01-4e70-aa21-a6827e1625a4')
INSERT INTO cuentas_bancarias (id,codigo,nro_cuenta,nro_cci,cvv,mes,year,saldo,enable,fecha,banco_id,moneda_id,usuario_id) VALUES ('cae8d7bd-16b7-488c-9e46-c5f2625f657a' ,'CTB000001','PE12-2222-2222-22-2222222222','333-333-333333333333-33','123','01','2022',5000,1,NOW(),1,1,'0173a9dc-1a01-4e70-aa21-a6827e1625a4');
INSERT INTO cuentas_bancarias (id,codigo,nro_cuenta,nro_cci,cvv,mes,year,saldo,enable,fecha,banco_id,moneda_id,usuario_id) VALUES ('d79b41eb-4939-4fb8-bfe9-f71c894f64e8' ,'CTB000002','PE12-2222-2222-22-2222222221','333-333-333333333333-32','123','01','2022',100000,1,NOW(),1,2,'cd7ae398-d090-4127-bb29-02a90bfe8828');
INSERT INTO tipo_transacciones (id,nombre) VALUES(1,'Deposito')
INSERT INTO tipo_transacciones (id,nombre) VALUES(2,'Retiro')