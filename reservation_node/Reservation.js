// reservation.js
require("dotenv").config();
const express = require("express");
const { Pool } = require("pg");

const app = express();
app.use(express.json());

// =============================
// 1) Connexion PostgreSQL
// =============================
const pool = new Pool({

  host: process.env.DB_HOST || "localhost",
  port: process.env.DB_PORT ? Number(process.env.DB_PORT) : 5432,
  user: process.env.DB_USER || "postgres",
  password: process.env.DB_PASS || "postgres",
  database: process.env.DB_NAME || "hotel"
});

pool
  .connect()
  .then((client) => {
    console.log("✅ Connexion PostgreSQL OK (reservation)");
    client.release();
  })
  .catch((err) => {
    console.error("❌ Erreur connexion PostgreSQL :", err.message);
    process.exit(1);
  });

// =============================
// 2) Routes pour les clients
// =============================

// GET /clients : liste des clients
app.get("/clients", async (req, res) => {
  try {
    const result = await pool.query("SELECT * FROM client ORDER BY id_client");
    res.json(result.rows);
  } catch (err) {
    console.error("GET /clients erreur :", err);
    res.status(500).json({ error: "Erreur serveur" });
  }
});

// POST /clients : ajouter un client
app.post("/clients", async (req, res) => {
  const { nom, tel, email } = req.body;

  if (!nom) {
    return res.status(400).json({ error: "nom est obligatoire" });
  }

  try {
    const result = await pool.query(
      "INSERT INTO client (nom, tel, email) VALUES ($1, $2, $3) RETURNING *",
      [nom, tel || null, email || null]
    );
    res.status(201).json(result.rows[0]);
  } catch (err) {
    console.error("POST /clients erreur :", err);
    res.status(500).json({ error: "Erreur serveur" });
  }
});

// =============================
// 3) Routes pour les chambres
// =============================

// GET /chambres : liste des chambres
app.get("/chambres", async (req, res) => {
  try {
    const result = await pool.query("SELECT * FROM chambre ORDER BY num");
    res.json(result.rows);
  } catch (err) {
    console.error("GET /chambres erreur :", err);
    res.status(500).json({ error: "Erreur serveur" });
  }
});

// POST /chambres : ajouter une chambre
app.post("/chambres", async (req, res) => {
  const { type, prix, etat } = req.body;

  if (!type || prix === undefined) {
    return res
      .status(400)
      .json({ error: "type et prix sont obligatoires" });
  }

  try {
    const result = await pool.query(
      "INSERT INTO chambre (type, prix, etat) VALUES ($1, $2, $3) RETURNING *",
      [type, prix, etat === undefined ? true : etat]
    );
    res.status(201).json(result.rows[0]);
  } catch (err) {
    console.error("POST /chambres erreur :", err);
    res.status(500).json({ error: "Erreur serveur" });
  }
});

// =============================
// 4) Routes pour les réservations
// =============================

// GET /reservations : liste simple
app.get("/reservations", async (req, res) => {
  try {
    const result = await pool.query(
      "SELECT * FROM reservation ORDER BY id_reservation DESC"
    );
    res.json(result.rows);
  } catch (err) {
    console.error("GET /reservations erreur :", err);
    res.status(500).json({ error: "Erreur serveur" });
  }
});

// GET /reservations/details : avec jointure client + chambre
app.get("/reservations/details", async (req, res) => {
  try {
    const result = await pool.query(
      `SELECT r.id_reservation,
              r.datedebut,
              r.datefin,
              r.prix,
              c.num,
              c.type,
              c.prix AS prix_chambre,
              c.etat,
              cl.id_client,
              cl.nom,
              cl.tel,
              cl.email
       FROM reservation r
       JOIN chambre c ON r.num = c.num
       JOIN client cl ON r.id_client = cl.id_client
       ORDER BY r.id_reservation DESC`
    );
    res.json(result.rows);
  } catch (err) {
    console.error("GET /reservations/details erreur :", err);
    res.status(500).json({ error: "Erreur serveur" });
  }
});

// GET /reservations/:id
app.get("/reservations/:id", async (req, res) => {
  const { id } = req.params;

  try {
    const result = await pool.query(
      "SELECT * FROM reservation WHERE id_reservation = $1",
      [id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: "Réservation non trouvée" });
    }

    res.json(result.rows[0]);
  } catch (err) {
    console.error("GET /reservations/:id erreur :", err);
    res.status(500).json({ error: "Erreur serveur" });
  }
});

// POST /reservations : créer une nouvelle réservation
app.post("/reservations", async (req, res) => {
  const {
    datedebut,
    datefin,
    num,        // numéro de chambre
    id_client,
    prix
  } = req.body;

  if (!datedebut || !datefin || !num || !id_client || prix === undefined) {
    return res.status(400).json({
      error:
        "datedebut, datefin, num, id_client et prix sont obligatoires"
    });
  }

  const client = await pool.connect();
  try {
    await client.query("BEGIN");

    // Vérifier que le client existe
    const clientRes = await client.query(
      "SELECT * FROM client WHERE id_client = $1",
      [id_client]
    );
    if (clientRes.rows.length === 0) {
      await client.query("ROLLBACK");
      return res.status(400).json({ error: "Client inexistant" });
    }

    // Vérifier que la chambre existe
    const chambreRes = await client.query(
      "SELECT * FROM chambre WHERE num = $1",
      [num]
    );
    if (chambreRes.rows.length === 0) {
      await client.query("ROLLBACK");
      return res.status(400).json({ error: "Chambre inexistante" });
    }

    // (Optionnel) Vérifier qu'il n'y a pas déjà une réservation qui chevauche ces dates

    const insertRes = await client.query(
      `INSERT INTO reservation
       (datedebut, datefin, num, id_client, prix)
       VALUES ($1, $2, $3, $4, $5)
       RETURNING *`,
      [datedebut, datefin, num, id_client, prix]
    );

    await client.query("COMMIT");
    res.status(201).json(insertRes.rows[0]);
  } catch (err) {
    await client.query("ROLLBACK");
    console.error("POST /reservations erreur :", err);
    res.status(500).json({ error: "Erreur serveur" });
  } finally {
    client.release();
  }
});

// PUT /reservations/:id : modifier une réservation
app.put("/reservations/:id", async (req, res) => {
  const { id } = req.params;
  const { datedebut, datefin, num, id_client, prix } = req.body;

  try {
    const result = await pool.query(
      `UPDATE reservation
       SET datedebut = COALESCE($1, datedebut),
           datefin   = COALESCE($2, datefin),
           num       = COALESCE($3, num),
           id_client = COALESCE($4, id_client),
           prix      = COALESCE($5, prix)
       WHERE id_reservation = $6`,
      [datedebut, datefin, num, id_client, prix, id]
    );

    if (result.rowCount === 0) {
      return res.status(404).json({ error: "Réservation non trouvée" });
    }

    res.json({ message: "Réservation mise à jour" });
  } catch (err) {
    console.error("PUT /reservations/:id erreur :", err);
    res.status(500).json({ error: "Erreur serveur" });
  }
});

// DELETE /reservations/:id : supprimer une réservation
app.delete("/reservations/:id", async (req, res) => {
  const { id } = req.params;

  try {
    const result = await pool.query(
      "DELETE FROM reservation WHERE id_reservation = $1",
      [id]
    );

    if (result.rowCount === 0) {
      return res.status(404).json({ error: "Réservation non trouvée" });
    }

    res.json({ message: "Réservation supprimée" });
  } catch (err) {
    console.error("DELETE /reservations/:id erreur :", err);
    res.status(500).json({ error: "Erreur serveur" });
  }
});

// =============================
// 5) Lancement du serveur
// =============================
const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`🚀 Service réservation démarré sur le port ${PORT}`);
});
