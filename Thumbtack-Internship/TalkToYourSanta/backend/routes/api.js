import express from 'express';
import messagesRouter from './messages';
import loginRouter from './login';
import pairsRouter from './pairs';
import meRouter from './me';

const router = express.Router();

router.use('/login', loginRouter);
router.use('/messages', messagesRouter);
router.use('/pairs', pairsRouter);
router.use('/me', meRouter);

export default router;
