"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.verifyToken = exports.createToken = void 0;
var jsonwebtoken_1 = require("jsonwebtoken");
var secret = require('../constants/constants');
var createToken = function (user) {
    return jsonwebtoken_1.default.sign(user, secret, { expiresIn: '1h' });
};
exports.createToken = createToken;
var verifyToken = function (token) {
    try {
        return jsonwebtoken_1.default.verify(token, secret);
    }
    catch (error) {
        console.error('Invalid or expired token:', error);
        return null;
    }
};
exports.verifyToken = verifyToken;
