"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g = Object.create((typeof Iterator === "function" ? Iterator : Object).prototype);
    return g.next = verb(0), g["throw"] = verb(1), g["return"] = verb(2), typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (g && (g = 0, op[0] && (_ = 0)), _) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.deleteUser = exports.logout = exports.login = exports.signup = void 0;
var prismaClient_1 = require("../constants/prismaClient");
var hashing_1 = require("../services/hashing");
var jwt_1 = require("../services/jwt");
var passwordCheck_1 = require("../services/passwordCheck");
var signup = function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var _a, username, password, passwordCheckResponse, hashedPassword, user, jwt, error_1;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _a = req.body, username = _a.username, password = _a.password;
                // Check if username and password are provided
                if (!username || !password) {
                    return [2 /*return*/, res.status(400).json({ error: 'Username and password are required' })];
                }
                passwordCheckResponse = (0, passwordCheck_1.passwordCheck)(password);
                if (passwordCheckResponse !== 'Password is valid') {
                    return [2 /*return*/, res.status(400).json({ error: passwordCheckResponse })];
                }
                _b.label = 1;
            case 1:
                _b.trys.push([1, 4, , 5]);
                return [4 /*yield*/, (0, hashing_1.hashPassword)(password)
                    // Create a new user in the database
                ];
            case 2:
                hashedPassword = _b.sent();
                return [4 /*yield*/, prismaClient_1.default.user.create({
                        data: {
                            username: username,
                            password: hashedPassword
                        }
                    })
                    // Generate a JWT token
                ];
            case 3:
                user = _b.sent();
                jwt = (0, jwt_1.createToken)({
                    username: user.username,
                    createdAt: user.createdAt,
                    updatedAt: user.updatedAt
                });
                // Set the JWT token in a cookie
                res.cookie('jwt', jwt, {
                    httpOnly: true,
                    secure: true,
                    sameSite: 'none'
                });
                // Send the user object without sensitive fields like password
                res.status(200).json({
                    username: user.username,
                    token: jwt
                });
                return [3 /*break*/, 5];
            case 4:
                error_1 = _b.sent();
                // Handle errors gracefully
                res.status(500).json({ error: 'An error occurred during signup' });
                return [3 /*break*/, 5];
            case 5: return [2 /*return*/];
        }
    });
}); };
exports.signup = signup;
var login = function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var _a, username, password, user, isPasswordValid, jwt, error_2;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _a = req.body, username = _a.username, password = _a.password;
                if (!username || !password) {
                    return [2 /*return*/, res.status(400).json({ error: 'Username and password are required' })];
                }
                _b.label = 1;
            case 1:
                _b.trys.push([1, 4, , 5]);
                return [4 /*yield*/, prismaClient_1.default.user.findUnique({
                        where: { username: username }
                    })];
            case 2:
                user = _b.sent();
                if (!user) {
                    return [2 /*return*/, res.status(400).json({ error: 'Invalid username or password' })];
                }
                return [4 /*yield*/, (0, hashing_1.comparePassword)(password, user.password)];
            case 3:
                isPasswordValid = _b.sent();
                if (!isPasswordValid) {
                    return [2 /*return*/, res.status(400).json({ error: 'Invalid username or password' })];
                }
                jwt = (0, jwt_1.createToken)({
                    username: user.username,
                    createdAt: user.createdAt,
                    updatedAt: user.updatedAt
                });
                res.cookie('jwt', jwt, {
                    httpOnly: true,
                    secure: true,
                    sameSite: 'none'
                });
                return [2 /*return*/, res.status(200).json({
                        username: user.username,
                        token: jwt
                    })];
            case 4:
                error_2 = _b.sent();
                console.error('Error during login:', error_2);
                return [2 /*return*/, res.status(500).json({ error: 'An error occurred during login' })];
            case 5: return [2 /*return*/];
        }
    });
}); };
exports.login = login;
var logout = function (req, res) {
    // Clear the JWT cookie
    if (!req.cookies || !req.cookies.jwt) {
        return res.status(400).json({ error: 'User is not logged in' });
    }
    res.clearCookie('jwt');
    // Send a success response
    res.status(200).json({ message: 'Logged out successfully' });
};
exports.logout = logout;
var deleteUser = function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var token, decodedToken, username, user, error_3;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                if (!req.cookies || !req.cookies.jwt) {
                    return [2 /*return*/, res.status(400).json({ error: 'JWT token missing' })];
                }
                token = req.cookies.jwt;
                _a.label = 1;
            case 1:
                _a.trys.push([1, 4, , 5]);
                decodedToken = (0, jwt_1.verifyToken)(token);
                if (!decodedToken || typeof decodedToken !== 'object') {
                    return [2 /*return*/, res.status(400).json({ error: 'Invalid or expired JWT token' })];
                }
                username = decodedToken.username;
                if (!username) {
                    return [2 /*return*/, res.status(400).json({ error: 'Invalid JWT payload' })];
                }
                return [4 /*yield*/, prismaClient_1.default.user.findUnique({
                        where: { username: username }
                    })];
            case 2:
                user = _a.sent();
                if (!user) {
                    return [2 /*return*/, res.status(404).json({ error: 'User not found' })];
                }
                // Delete the user from the database
                return [4 /*yield*/, prismaClient_1.default.user.delete({
                        where: { username: username }
                    })];
            case 3:
                // Delete the user from the database
                _a.sent();
                return [2 /*return*/, res.status(200).json({ message: 'User deleted successfully' })];
            case 4:
                error_3 = _a.sent();
                console.error('Error during user deletion:', error_3);
                return [2 /*return*/, res
                        .status(500)
                        .json({ error: 'An error occurred during user deletion' })];
            case 5: return [2 /*return*/];
        }
    });
}); };
exports.deleteUser = deleteUser;
