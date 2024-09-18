"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var cors_1 = require("cors");
var express_1 = require("express");
var authRoutes_1 = require("./routes/authRoutes");
var app = (0, express_1.default)();
var options = {
    origin: 'http://localhost:8080'
};
app.use((0, cors_1.default)(options));
app.use(express_1.default.json());
app.get('/ping', function (req, res) {
    res.json('pong');
});
app.use('/', authRoutes_1.default);
var PORT = process.env.PORT || 8500;
app.listen(PORT, function () {
    console.log("Server is running on port ".concat(PORT));
});
