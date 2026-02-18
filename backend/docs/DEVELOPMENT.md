# Guía de Desarrollo Backend

## Requisitos

- Java 21
- Maven (o usar `./mvnw`)
- Git
- IntelliJ IDEA

## Ejecutar en local

Ruta:

- `/Users/tinus/Developer/scalevision/backend`

Comandos:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

## Estrategia de ramas

Base de trabajo backend:

- `scalevision-backend`

Formato de rama por actividad:

- `feature/backend-activity-<numero>-<nombre-corto>`

Ejemplo:

```bash
git checkout scalevision-backend
git pull origin scalevision-backend
git checkout -b feature/backend-activity-11-documentation
```

## Flujo recomendado para cada actividad

1. Crear rama `feature/*` desde `scalevision-backend`.
2. Implementar solo la actividad asignada.
3. Ejecutar tests.
4. Commit con mensaje claro.
5. Push de la rama.
6. Crear PR: `base=scalevision-backend`, `compare=feature/...`.
7. Merge PR.
8. Borrar rama remota y local.

## Comandos útiles de limpieza después de merge

```bash
git checkout scalevision-backend
git pull origin scalevision-backend
git branch -d feature/backend-activity-11-documentation
git push origin --delete feature/backend-activity-11-documentation
```

Si el remoto ya no existe:

```text
error: remote ref does not exist
```

No es error crítico. Solo significa que ya fue eliminada.

## PR de control a `dev` (cuando aplique)

Si están trabajando por integración:

- PR 1: `feature/*` -> `scalevision-backend`
- PR 2: `scalevision-backend` -> `dev`

Nota:

- Si `dev` o `main` están protegidas y no permiten bypass a admin, siempre se necesita review para merge.

## Convención para comentarios de control en PR

Ejemplo:

```text
[BACKEND] Activity 11 completed
- Docs: README, ARCHITECTURE, DEVELOPMENT, API
- Polling documented as official MVP strategy
- Ready for review
```
