const express = require('express');
const { engine } = require('express-handlebars');
const bodyParser = require('body-parser');
const firebaseAdmin = require('firebase-admin');
const path = require('path');
const app = express();
const port = 8088;

// Configuração do Firebase Admin SDK
const serviceAccount = require('./config/firebaseConfig.json');
firebaseAdmin.initializeApp({
    credential: firebaseAdmin.credential.cert(serviceAccount)
});

const db = firebaseAdmin.firestore();

// Configuração do Express
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.set('view engine', 'handlebars');
app.set('views', path.join(__dirname, 'views'));
app.engine('handlebars', engine());
app.use(express.static('img'));

    // Rota para redirecionar a página inicial para a tela de login
    app.get('/', (req, res) => {
        res.redirect('/login'); // Redireciona para a página de login
    });

    // Rota para exibir a lista de livros
    app.get('/home', async (req, res) => {
        const livrosRef = db.collection('livros');
        const snapshot = await livrosRef.get();
        const livros = snapshot.docs.map(doc => ({
            id: doc.id,
            ...doc.data()
        }));

        res.render('home', { livros });
    });

    // Rotas para renderizar páginas
    app.get('/register', (req, res) => {
        res.render('register');
    });

    app.get('/login', (req, res) => {
        res.render('login');
    });

    // Rota para criar um usuário
    app.post('/register', async (req, res) => {
        try {
            const { email, password } = req.body;

            // Validações no servidor
            if (!email.endsWith('@gmail.com')) {
                return res.status(400).send('O email deve ser um Gmail (terminar com @gmail.com).');
            }
            if (password.length < 8) {
                return res.status(400).send('A senha deve ter no mínimo 8 caracteres.');
            }

            // const newUser = await firebaseAdmin.auth().createUser({ email, password });
            const newUser = firebaseAdmin.auth().createUser({
                email: email,
                password: password,
            });

            //await db.collection('usuarios').doc(newUser.uid).set({ email });
            res.redirect('/login');

        } catch (error) {
            console.log(error);
            console.error("Erro ao cadastrar:", error);
            // res.status(400).send("Erro ao cadastrar.");
        }
    });

    // Rota para autenticar usuário
    app.post('/login', async (req, res) => {
        const { email, senha } = req.body;

        // Validações no servidor
        if (!email.endsWith('@gmail.com')) {
            return res.status(400).send('O email deve ser um Gmail (terminar com @gmail.com).');
        }
        if (senha.length < 8) {
            return res.status(400).send('A senha deve ter no mínimo 8 caracteres.');
        }

        try {
            const userRecord = await admin.auth().getUserByEmail(email);
            const user = await admin.auth().verifyPassword(userRecord.uid, senha);
            if (user) {
                res.send("Login realizado com sucesso!");
            } else {
                res.status(401).send("Credenciais inválidas.");
            }
        } catch (error) {
            console.error("Erro no login:", error);
            res.status(400).send("Erro no login.");
        }
    });

    // Rota para exibir o formulário de adicionar livro
    app.get('/livro/create', (req, res) => {
        res.render('create');
    });

    // Rota para salvar um novo livro no Firestore
    app.post('/livro/create', async (req, res) => {
        const { titulo, autor, ano } = req.body;
        try {
            await db.collection('livros').add({ titulo, autor, ano });
            res.redirect('/home');
        } catch (error) {
            console.error('Erro ao adicionar livro:', error);
            res.status(500).send('Erro ao adicionar o livro');
        }
    });

    // Rota para editar um livro
    app.get('/livro/edit/:id', async (req, res) => {
        const livroId = req.params.id;
        try {
            const livroDoc = await db.collection('livros').doc(livroId).get();

            if (!livroDoc.exists) {
                return res.status(404).send('Livro não encontrado');
            }

            res.render('edit', {
                livro: livroDoc.data(),
                id: livroId
            });
        } catch (error) {
            console.error('Erro ao buscar o livro:', error);
            res.status(500).send('Erro ao recuperar o livro');
        }
    });

    // Rota para atualizar um livro
    app.post('/livro/edit/:id', async (req, res) => {
        const livroId = req.params.id;
        const { titulo, autor, ano } = req.body;

        try {
            await db.collection('livros').doc(livroId).update({
                titulo: titulo,
                autor: autor,
                ano: ano
            });

            res.redirect('/home');
        } catch (error) {
            console.error('Erro ao atualizar livro:', error);
            res.status(500).send('Erro ao atualizar o livro');
        }
    });

    // Rota para deletar um livro
    app.get('/livro/deletar/:id', async (req, res) => {
        await db.collection('livros').doc(req.params.id).delete();
        res.redirect('/home');
    });

    app.listen(port, () => {
        console.log(`Servidor rodando em http://localhost:${port}`);
    });