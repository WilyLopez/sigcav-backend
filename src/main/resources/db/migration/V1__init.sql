CREATE TABLE usuario (
    id                  BIGSERIAL PRIMARY KEY,
    nombre_completo     VARCHAR(200) NOT NULL,
    nombre_usuario      VARCHAR(100) NOT NULL UNIQUE,
    correo              VARCHAR(150) NOT NULL UNIQUE,
    contrasena_hash     VARCHAR(255) NOT NULL,
    activo              BOOLEAN NOT NULL DEFAULT TRUE,
    rol                 VARCHAR(30) NOT NULL DEFAULT 'ASISTENTE_ADMINISTRATIVO'
                        CHECK (rol IN ('ADMINISTRADOR', 'ASISTENTE_ADMINISTRATIVO')),
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado_en      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE sesion_usuario (
    id                  BIGSERIAL PRIMARY KEY,
    usuario_id          BIGINT NOT NULL REFERENCES usuario(id),
    token_sesion        VARCHAR(512) NOT NULL UNIQUE,
    ultimo_acceso       TIMESTAMP NOT NULL DEFAULT NOW(),
    ip_origen           VARCHAR(45),
    activa              BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE refresh_token (
    id                  BIGSERIAL PRIMARY KEY,
    usuario_id          BIGINT NOT NULL REFERENCES usuario(id),
    token               VARCHAR(512) NOT NULL UNIQUE,
    expira_en           TIMESTAMP NOT NULL,
    revocado            BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE parametro_sistema (
    id                  BIGSERIAL PRIMARY KEY,
    clave               VARCHAR(100) NOT NULL UNIQUE,
    valor               VARCHAR(500) NOT NULL,
    descripcion         VARCHAR(300),
    tipo_dato           SMALLINT NOT NULL DEFAULT 0,
    actualizado_en      TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado_por_id  BIGINT REFERENCES usuario(id)
);

CREATE TABLE categoria_producto (
    id                  BIGSERIAL PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL UNIQUE,
    activo              BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE categoria_proveedor (
    id                  BIGSERIAL PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL UNIQUE,
    activo              BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE cliente (
    id                      BIGSERIAL PRIMARY KEY,
    tipo_documento          VARCHAR(3) NOT NULL CHECK (tipo_documento IN ('RUC', 'DNI')),
    numero_documento        VARCHAR(11) NOT NULL UNIQUE,
    nombre_razon_social     VARCHAR(200) NOT NULL,
    telefono_principal      VARCHAR(20) NOT NULL,
    telefono_secundario     VARCHAR(20),
    correo                  VARCHAR(150) NOT NULL,
    direccion_entrega       VARCHAR(300) NOT NULL,
    nombre_contacto_ref     VARCHAR(200),
    observaciones           TEXT,
    activo                  BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en               TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado_en          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE contacto_cliente (
    id                  BIGSERIAL PRIMARY KEY,
    cliente_id          BIGINT NOT NULL REFERENCES cliente(id),
    nombre              VARCHAR(200) NOT NULL,
    telefono            VARCHAR(20),
    correo              VARCHAR(150),
    cargo               VARCHAR(100),
    es_principal        BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE proveedor (
    id                      BIGSERIAL PRIMARY KEY,
    tipo_documento          VARCHAR(3) NOT NULL CHECK (tipo_documento IN ('RUC', 'DNI')),
    numero_documento        VARCHAR(11) NOT NULL UNIQUE,
    nombre_razon_social     VARCHAR(200) NOT NULL,
    categoria_proveedor_id  BIGINT NOT NULL REFERENCES categoria_proveedor(id),
    telefono                VARCHAR(20) NOT NULL,
    correo                  VARCHAR(150),
    direccion               VARCHAR(300),
    nombre_representante    VARCHAR(200),
    observaciones           TEXT,
    activo                  BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en               TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado_en          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE cotizacion (
    id                      BIGSERIAL PRIMARY KEY,
    numero_cotizacion       VARCHAR(20) NOT NULL UNIQUE,
    cliente_id              BIGINT NOT NULL REFERENCES cliente(id),
    fecha_emision           DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_vencimiento       DATE NOT NULL,
    descripcion_producto    TEXT NOT NULL,
    tipo_impresion          VARCHAR(200),
    material                VARCHAR(200),
    dimensiones             VARCHAR(200),
    acabados                VARCHAR(200),
    cantidad                INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario         NUMERIC(12,2) NOT NULL CHECK (precio_unitario >= 0),
    descuento_porcentaje    NUMERIC(5,2) NOT NULL DEFAULT 0 CHECK (descuento_porcentaje >= 0 AND descuento_porcentaje <= 100),
    recargo_porcentaje      NUMERIC(5,2) NOT NULL DEFAULT 0 CHECK (recargo_porcentaje >= 0),
    subtotal                NUMERIC(12,2) NOT NULL,
    aplica_igv              BOOLEAN NOT NULL DEFAULT FALSE,
    igv_monto               NUMERIC(12,2) NOT NULL DEFAULT 0,
    total                   NUMERIC(12,2) NOT NULL,
    tiempo_entrega_estimado VARCHAR(100),
    condiciones_pago        TEXT,
    observaciones           TEXT,
    estado                  VARCHAR(20) NOT NULL DEFAULT 'BORRADOR'
                                CHECK (estado IN ('BORRADOR','ENVIADA','APROBADA','RECHAZADA','VENCIDA')),
    convertida_en_pedido    BOOLEAN NOT NULL DEFAULT FALSE,
    creado_por_id           BIGINT REFERENCES usuario(id),
    creado_en               TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado_en          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE plantilla_cotizacion (
    id                      BIGSERIAL PRIMARY KEY,
    nombre_plantilla        VARCHAR(200) NOT NULL,
    descripcion_producto    TEXT NOT NULL,
    tipo_impresion          VARCHAR(200),
    material                VARCHAR(200),
    dimensiones             VARCHAR(200),
    acabados                VARCHAR(200),
    condiciones_pago        TEXT,
    observaciones           TEXT,
    activo                  BOOLEAN NOT NULL DEFAULT TRUE,
    creado_por_id           BIGINT REFERENCES usuario(id),
    creado_en               TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE pedido (
    id                          BIGSERIAL PRIMARY KEY,
    numero_pedido               VARCHAR(20) NOT NULL UNIQUE,
    cotizacion_id               BIGINT UNIQUE REFERENCES cotizacion(id),
    cliente_id                  BIGINT NOT NULL REFERENCES cliente(id),
    categoria_producto_id       BIGINT NOT NULL REFERENCES categoria_producto(id),
    descripcion                 TEXT NOT NULL CHECK (LENGTH(descripcion) >= 10),
    especificaciones            TEXT NOT NULL,
    cantidad                    INTEGER NOT NULL CHECK (cantidad > 0),
    precio_venta                NUMERIC(12,2) NOT NULL CHECK (precio_venta > 0),
    fecha_ingreso               DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_entrega_comprometida  DATE NOT NULL,
    estado                      VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                                    CHECK (estado IN ('PENDIENTE','EN_PRODUCCION','CONTROL_CALIDAD','LISTO_ENTREGA','ENTREGADO','FACTURADO','ANULADO')),
    estado_pago                 VARCHAR(25) NOT NULL DEFAULT 'SIN_ADELANTO'
                                    CHECK (estado_pago IN ('SIN_ADELANTO','ADELANTO_REGISTRADO','PAGADO_COMPLETAMENTE')),
    costo_total                 NUMERIC(12,2) NOT NULL DEFAULT 0,
    ganancia_bruta              NUMERIC(12,2) GENERATED ALWAYS AS (precio_venta - costo_total) STORED,
    margen_ganancia_porcentaje  NUMERIC(7,4) GENERATED ALWAYS AS (
                                    CASE WHEN precio_venta > 0
                                    THEN ((precio_venta - costo_total) / precio_venta) * 100
                                    ELSE NULL END
                                ) STORED,
    creado_por_id               BIGINT REFERENCES usuario(id),
    creado_en                   TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado_en              TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fecha_entrega_valida CHECK (fecha_entrega_comprometida >= fecha_ingreso)
);

CREATE TABLE historial_estado_pedido (
    id                  BIGSERIAL PRIMARY KEY,
    pedido_id           BIGINT NOT NULL REFERENCES pedido(id),
    estado_anterior     VARCHAR(20),
    estado_nuevo        VARCHAR(20) NOT NULL,
    justificacion       TEXT,
    usuario_id          BIGINT REFERENCES usuario(id),
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE nota_interna_pedido (
    id                  BIGSERIAL PRIMARY KEY,
    pedido_id           BIGINT NOT NULL REFERENCES pedido(id),
    contenido           TEXT NOT NULL,
    usuario_id          BIGINT REFERENCES usuario(id),
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE compra (
    id                      BIGSERIAL PRIMARY KEY,
    proveedor_id            BIGINT NOT NULL REFERENCES proveedor(id),
    fecha_compra            DATE NOT NULL,
    numero_comprobante_proveedor VARCHAR(100),
    tipo_comprobante_proveedor   VARCHAR(20) CHECK (tipo_comprobante_proveedor IN ('FACTURA','BOLETA','TICKET','OTRO')),
    total                   NUMERIC(12,2) NOT NULL CHECK (total > 0),
    observaciones           TEXT,
    registrado_por_id       BIGINT REFERENCES usuario(id),
    creado_en               TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado_en          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE item_compra (
    id                  BIGSERIAL PRIMARY KEY,
    compra_id           BIGINT NOT NULL REFERENCES compra(id),
    nombre_material     VARCHAR(200) NOT NULL,
    unidad_medida       VARCHAR(30) NOT NULL,
    cantidad            NUMERIC(12,4) NOT NULL CHECK (cantidad > 0),
    precio_unitario     NUMERIC(12,4) NOT NULL CHECK (precio_unitario >= 0),
    subtotal            NUMERIC(12,2) NOT NULL,
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE compra_pedido (
    id                  BIGSERIAL PRIMARY KEY,
    compra_id           BIGINT NOT NULL REFERENCES compra(id),
    pedido_id           BIGINT NOT NULL REFERENCES pedido(id),
    monto_asignado      NUMERIC(12,2) NOT NULL CHECK (monto_asignado > 0),
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (compra_id, pedido_id)
);

CREATE TABLE gasto_pedido (
    id                          BIGSERIAL PRIMARY KEY,
    pedido_id                   BIGINT NOT NULL REFERENCES pedido(id),
    tipo_gasto                  VARCHAR(25) NOT NULL
                                    CHECK (tipo_gasto IN ('MANO_OBRA_EXTERNA','SERVICIO_EXTERNO','TRANSPORTE','OTRO')),
    descripcion                 VARCHAR(300) NOT NULL,
    proveedor_id                BIGINT REFERENCES proveedor(id),
    fecha_gasto                 DATE NOT NULL,
    monto                       NUMERIC(12,2) NOT NULL CHECK (monto > 0),
    numero_comprobante_proveedor VARCHAR(100),
    observaciones               TEXT,
    registrado_por_id           BIGINT REFERENCES usuario(id),
    creado_en                   TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado_en              TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE pago (
    id                  BIGSERIAL PRIMARY KEY,
    pedido_id           BIGINT NOT NULL REFERENCES pedido(id),
    tipo_pago           VARCHAR(10) NOT NULL CHECK (tipo_pago IN ('ADELANTO','SALDO')),
    monto               NUMERIC(12,2) NOT NULL CHECK (monto > 0),
    fecha_pago          DATE NOT NULL,
    forma_pago          VARCHAR(20) NOT NULL
                            CHECK (forma_pago IN ('EFECTIVO','TRANSFERENCIA','YAPE_PLIN','OTRO')),
    observacion         TEXT,
    registrado_por_id   BIGINT REFERENCES usuario(id),
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (pedido_id, tipo_pago)
);

CREATE TABLE nota_correccion_pago (
    id                  BIGSERIAL PRIMARY KEY,
    pago_id             BIGINT NOT NULL REFERENCES pago(id),
    justificacion       TEXT NOT NULL,
    usuario_id          BIGINT REFERENCES usuario(id),
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE serie_comprobante (
    id                  BIGSERIAL PRIMARY KEY,
    tipo_comprobante    VARCHAR(15) NOT NULL UNIQUE
                            CHECK (tipo_comprobante IN ('FACTURA','BOLETA','NOTA_VENTA')),
    serie               VARCHAR(10) NOT NULL,
    ultimo_correlativo  INTEGER NOT NULL DEFAULT 0,
    actualizado_en      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE comprobante (
    id                      BIGSERIAL PRIMARY KEY,
    pedido_id               BIGINT NOT NULL UNIQUE REFERENCES pedido(id),
    tipo_comprobante        VARCHAR(15) NOT NULL
                                CHECK (tipo_comprobante IN ('FACTURA','BOLETA','NOTA_VENTA')),
    serie                   VARCHAR(10) NOT NULL,
    correlativo             INTEGER NOT NULL,
    numero_completo         VARCHAR(30) NOT NULL UNIQUE,
    fecha_emision           DATE NOT NULL DEFAULT CURRENT_DATE,
    cliente_id              BIGINT NOT NULL REFERENCES cliente(id),
    descripcion_servicio    TEXT NOT NULL,
    subtotal                NUMERIC(12,2) NOT NULL,
    igv_porcentaje          NUMERIC(5,2) NOT NULL DEFAULT 0,
    igv_monto               NUMERIC(12,2) NOT NULL DEFAULT 0,
    total                   NUMERIC(12,2) NOT NULL,
    forma_pago              VARCHAR(20) NOT NULL
                                CHECK (forma_pago IN ('EFECTIVO','TRANSFERENCIA','YAPE_PLIN','OTRO')),
    anulado                 BOOLEAN NOT NULL DEFAULT FALSE,
    emitido_por_id          BIGINT REFERENCES usuario(id),
    creado_en               TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE anulacion_comprobante (
    id                  BIGSERIAL PRIMARY KEY,
    comprobante_id      BIGINT NOT NULL REFERENCES comprobante(id),
    justificacion       TEXT NOT NULL,
    usuario_id          BIGINT REFERENCES usuario(id),
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE log_auditoria (
    id                  BIGSERIAL PRIMARY KEY,
    usuario_id          BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
    accion              SMALLINT NOT NULL,
    entidad             SMALLINT NOT NULL,
    entidad_id          BIGINT,
    detalle             TEXT,
    ip_origen           VARCHAR(45),
    creado_en           TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_usuario_rol ON usuario(rol);

CREATE INDEX idx_cliente_numero_documento    ON cliente(numero_documento);
CREATE INDEX idx_cliente_nombre              ON cliente(nombre_razon_social);
CREATE INDEX idx_cliente_activo              ON cliente(activo);

CREATE INDEX idx_proveedor_numero_documento  ON proveedor(numero_documento);
CREATE INDEX idx_proveedor_nombre            ON proveedor(nombre_razon_social);
CREATE INDEX idx_proveedor_categoria         ON proveedor(categoria_proveedor_id);

CREATE INDEX idx_cotizacion_numero           ON cotizacion(numero_cotizacion);
CREATE INDEX idx_cotizacion_cliente          ON cotizacion(cliente_id);
CREATE INDEX idx_cotizacion_estado           ON cotizacion(estado);
CREATE INDEX idx_cotizacion_vencimiento      ON cotizacion(fecha_vencimiento);

CREATE INDEX idx_pedido_numero               ON pedido(numero_pedido);
CREATE INDEX idx_pedido_cliente              ON pedido(cliente_id);
CREATE INDEX idx_pedido_estado               ON pedido(estado);
CREATE INDEX idx_pedido_fecha_entrega        ON pedido(fecha_entrega_comprometida);
CREATE INDEX idx_pedido_categoria            ON pedido(categoria_producto_id);

CREATE INDEX idx_historial_pedido            ON historial_estado_pedido(pedido_id);
CREATE INDEX idx_nota_pedido                 ON nota_interna_pedido(pedido_id);

CREATE INDEX idx_compra_proveedor            ON compra(proveedor_id);
CREATE INDEX idx_compra_fecha               ON compra(fecha_compra);
CREATE INDEX idx_item_compra_compra          ON item_compra(compra_id);
CREATE INDEX idx_compra_pedido_compra        ON compra_pedido(compra_id);
CREATE INDEX idx_compra_pedido_pedido        ON compra_pedido(pedido_id);

CREATE INDEX idx_gasto_pedido_pedido         ON gasto_pedido(pedido_id);
CREATE INDEX idx_gasto_pedido_proveedor      ON gasto_pedido(proveedor_id);

CREATE INDEX idx_pago_pedido                 ON pago(pedido_id);
CREATE INDEX idx_comprobante_pedido          ON comprobante(pedido_id);
CREATE INDEX idx_comprobante_cliente         ON comprobante(cliente_id);
CREATE INDEX idx_comprobante_tipo            ON comprobante(tipo_comprobante);
CREATE INDEX idx_comprobante_numero          ON comprobante(numero_completo);

CREATE INDEX idx_log_auditoria_usuario       ON log_auditoria(usuario_id);
CREATE INDEX idx_log_auditoria_entidad       ON log_auditoria(entidad, entidad_id);
CREATE INDEX idx_log_auditoria_fecha         ON log_auditoria(creado_en);

INSERT INTO categoria_producto (nombre) VALUES
    ('Banners'),
    ('Tarjetas'),
    ('Uniformes'),
    ('Empaques'),
    ('Folletos'),
    ('Otros');

INSERT INTO categoria_proveedor (nombre) VALUES
    ('Papel'),
    ('Tintas'),
    ('Servicios externos'),
    ('Transporte'),
    ('Otros');

INSERT INTO serie_comprobante (tipo_comprobante, serie, ultimo_correlativo) VALUES
    ('FACTURA',    'F001', 0),
    ('BOLETA',     'B001', 0),
    ('NOTA_VENTA', 'NV001', 0);

INSERT INTO parametro_sistema (clave, valor, descripcion, tipo_dato) VALUES
    ('igv_porcentaje',              '18',   'Porcentaje de IGV aplicable en facturas',                           1),
    ('adelanto_minimo_porcentaje',  '50',   'Porcentaje mínimo de adelanto requerido para iniciar producción',   1),
    ('umbral_margen_verde',         '30',   'Margen de ganancia mínimo para semáforo verde (%)',                 1),
    ('umbral_margen_amarillo',      '15',   'Margen de ganancia mínimo para semáforo amarillo (%)',              1),
    ('dias_vencimiento_cotizacion', '7',    'Días por defecto para vencimiento de cotizaciones',                 2),
    ('dias_alerta_entrega',         '2',    'Días de anticipación para alerta de fecha de entrega próxima',      2),
    ('empresa_razon_social',        '',     'Razón social de la empresa emisora',                                0),
    ('empresa_ruc',                 '',     'RUC de la empresa emisora',                                         0),
    ('empresa_direccion',           '',     'Dirección de la empresa emisora',                                   0),
    ('empresa_telefono',            '',     'Teléfono de la empresa emisora',                                    0),
    ('empresa_correo',              '',     'Correo de la empresa emisora',                                      0),
    ('empresa_leyenda_comprobante', '',     'Leyenda que aparece al pie de los comprobantes',                    0)
ON CONFLICT (clave) DO NOTHING;