# InnovaHouse - Guía de Uso

## Descripción General
InnovaHouse es una aplicación Android de e-commerce para la compra y venta de electrodomésticos en Perú. La aplicación tiene dos roles principales: Administrador y Cliente.

## Credenciales de Prueba

### Administrador
- **Email**: admin@innova.com
- **Contraseña**: 1234

### Cliente de Prueba
Puedes registrar un nuevo cliente desde la opción "¿No tienes cuenta?"

## 🏠 Interfaz Principal

### Barra Superior (4 Botones)
1. **Casa** 🏠: Muestra todos los productos
2. **Aplicaciones** 📱: 
   - Admin → Pedidos de clientes
   - Cliente → Historial de pedidos
3. **Usuario** 👤: Perfil y edición de datos
4. **Carrito** 🛒:
   - Admin → Agregar nuevo producto
   - Cliente → Carrito de compras

## 📦 Funciones de Administrador

### 1. Dashboard (Casa)
- Ver todos los productos disponibles
- Buscar productos por nombre o descripción
- Hacer clic en un producto para ver detalles
- Ver botón "+" en la esquina inferior para agregar producto

### 2. Agregar Producto (+)
- Hacer clic en el botón "+"
- Completar:
  - Nombre del producto
  - Precio en soles (S/.)
  - Descripción detallada
  - Seleccionar imagen de la galería
- Clic en "Guardar producto"

### 3. Editar Producto
- Desde el dashboard, hacer clic en un producto
- En la pantalla de detalles, clic en "Editar"
- Modificar datos según necesario

### 4. Eliminar Producto
- Desde detalles del producto, clic en "Eliminar"
- Confirmar eliminación

### 5. Ver Pedidos (Aplicaciones)
- Clic en botón "Aplicaciones"
- Verá tabla de todos los pedidos de clientes
- Muestra: Cliente, Fecha, Total, Estado

### 6. Gestionar Clientes (Perfil)
- Clic en botón "Usuario"
- Clic en "Editar Datos de Admin"
  - Cambiar email
  - Cambiar contraseña (requiere actual)
- Clic en "Eliminar Cliente"
  - Ingrese email del cliente a eliminar
  - Confirme eliminación

## 🛍️ Funciones de Cliente

### 1. Ver Productos (Casa)
- Dashboard con todos los productos
- **Búsqueda y Filtro**: 
  - Escribir en buscador
  - Filtra por nombre y descripción
  - Se actualiza en tiempo real

### 2. Agregar al Carrito
- En la lista de productos, clic en "Agregar al Carrito"
- El producto se agrega automáticamente
- Confirmación por mensaje Toast

### 3. Ver Detalles
- Hacer clic sobre la tarjeta del producto
- Ver imagen grande
- Ver descripción completa

### 4. Carrito de Compras (Botón Carrito)
- Ver todos los productos agregados
- Precio total actualizado automáticamente
- Botón "Eliminar" para cada producto
- Botón "Proceder al Pago" para finalizar

### 5. Proceso de Pago
- Al clic "Proceder al Pago", seleccione método:
  - **Transferencia Bancaria**
  - **Tarjeta de Crédito/Débito**
  - **Billetera Digital** (Yape/Plin)
  - **Pago Contra Entrega**
- Clic "Confirmar Pago"
- El pedido se crea automáticamente
- Carrito se vacía

### 6. Ver Historial (Aplicaciones)
- Clic en botón "Aplicaciones"
- Verá tabla de todos sus pedidos
- Muestra: ID Pedido, Fecha, Total, Estado

### 7. Perfil y Edición
- Clic en botón "Usuario"
- Ver datos:
  - Nombre y Apellido
  - Correo electrónico
  - Teléfono
  - Rol (Cliente)
- Clic "Editar Mis Datos"
  - Cambiar correo
  - Cambiar contraseña (requiere contraseña actual)

## 🔐 Seguridad

### Validaciones de Registro
- ✅ Nombre: Requerido
- ✅ Apellido: Requerido
- ✅ Teléfono: Requerido (9 dígitos, solo números)
- ✅ Correo: Requerido y debe ser email válido
- ✅ Contraseña: Requerida (mínimo 6 caracteres)
- ✅ No permite registros con email duplicado

### Protecciones
- El email de admin no se puede eliminar
- El cliente no puede eliminar su propia cuenta
- Las contraseñas se verifican antes de cambios
- Los roles se validan en cada pantalla

## 💾 Base de Datos

### Tablas Principales
1. **usuarios**: Almacena clientes y administradores
2. **productos**: Catálogo de electrodomésticos
3. **carrito**: Productos temporales del usuario
4. **pedidos**: Órdenes realizadas
5. **detalles_pedido**: Items de cada pedido

## 🎨 Colores y Diseño

- **Azul Primario**: #1976D2 (Botones principales)
- **Verde Teal**: #00796B (Precio, acciones)
- **Verde**: #4CAF50 (Éxito, agregar)
- **Rojo**: #D32F2F (Eliminar, cancelar)
- **Naranja**: #FF9800 (Advertencia)

## ⚙️ Solución de Problemas

### Problema: "Credenciales incorrectas"
- **Solución**: Verifica que escribiste bien el email y contraseña
- Admin: admin@innova.com / 1234

### Problema: "El correo ya está registrado"
- **Solución**: Usa otro correo o inicia sesión si ya tienes cuenta

### Problema: El botón no funciona
- **Solución**: Verifica tu rol. Algunos botones son solo para admin o cliente

### Problema: Carrito vacío
- **Solución**: Agrega productos desde el dashboard
- Cada clic en "Agregar al Carrito" agrega una unidad

## 📱 Requisitos del Sistema
- Android 9 (API 28) o superior
- Acceso a galería de imágenes
- Conexión a internet (para características futuras)

## 🔄 Actualizar Datos
- Los datos se guardan automáticamente en SQLite
- Cierra sesión para ver cambios reflejados en otra cuenta

## ❓ Preguntas Frecuentes

**P: ¿Cuánto cuesta un producto?**
A: Depende del producto. Verifica el precio en la tarjeta.

**P: ¿Puedo cambiar la cantidad de productos?**
A: Actualmente cada clic agrega 1 unidad. Puedes eliminar y re-agregar.

**P: ¿Mis pedidos se guardan?**
A: Sí, todos tus pedidos aparecen en "Historial de Pedidos".

**P: ¿Puedo editar un pedido ya realizado?**
A: No, pero puedes eliminar productos del carrito antes de pagar.

---

**¡Gracias por usar InnovaHouse!** 🏪✨
