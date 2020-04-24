$(document).ready(function () {
    let searchParams = new URLSearchParams(window.location.search);
    // searchParams.has('sent') // true
    // let param = searchParams.get('sent')
    if (!searchParams.has('joinid')) {
        host();
    } else {
        join(searchParams.get('joinid'));
    }
});

var state = {
    gameId: null,
    ptoken: null,
    gameState: null,
    playerSide: 'white',
    isBlack: false, //for black we need to rotate the board
    validMoves: {}
};

function host() {

    $.ajax({
        type: "POST",
        url: "/api/game/host",
        data: JSON.stringify({}),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (data) {
            initGameParams(data);
        },
        failure: function (errMsg) {
            alert(errMsg);
        }
    });
}

function join(gameid) {
    $.ajax({
        type: "POST",
        url: "/api/game/" + gameid + "/join",
        data: JSON.stringify({}),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (data) {
            initGameParams(data);
        },
        error: function (errMsg) {
            if (errMsg.status === 404) {
                alert('Игра не найдена, проверьте ссылку или попробуйте ещё раз, пож');
            }
        }
    });
}

function await(timeoutms, max) {
    if (max === 0) {
        return;
    }

    $.ajax({
        type: "POST",
        url: "/api/game/" + state.gameId + "/wait-for-my-move",
        data: JSON.stringify({}),
        contentType: "application/json; charset=utf-8",
        headers: {'ptoken': state.ptoken},
        dataType: "json",
        error: function (err) {
            if (err.statusText === 'timeout') {
                await(timeoutms, max - 1);
            } else {
                console.log(err.status + " " + err.statusText);
            }
        },
        success: function (data) {
            handleBoardUpdate(data);
        },
        timeout: timeoutms
    });
}

function handleMove(to) {
    console.log("handle move " + state.selectedPosition + to);
    if (state.selectedPosition && to) {
        if (isPromotion(state.selectedPosition, to)) {
            state.to = to;
            promptPromotion();
        } else {
            move(state.selectedPosition, to);
        }
    }
}

function move(from, to, promotion) {
    const moveDto = {from: from, to: to, promotion: promotion};

    $.ajax({
        type: "POST",
        url: "/api/game/" + state.gameId + "/move",
        data: JSON.stringify(moveDto),
        contentType: "application/json; charset=utf-8",
        headers: {'ptoken': state.ptoken},
        dataType: "json",
        error: function (err) {
            console.log(err.status + " " + err.statusText);
        },
        success: function (data) {
            handleBoardUpdate(data);
            if (!state.gameState.gameFinished) {
                await(1000 * 60 * 5, 50);
            }
        }
    });
}

function isPromotion(from, to) {
    return (to.charAt(1) === '1' || to.charAt(1) === '8')
        && atPosition(from).attr('data-piece').includes('pawn');
}

function promptPromotion() {
    if (state.isBlack) {
        $("#promoteblack").show();
    } else {
        $("#promotewhite").show();
    }
}

function initGameParams(params) {
    state.gameId = params.id;
    state.ptoken = params.token;
    state.isBlack = "black" === params.playerSide.toLowerCase();
    state.playerSide = params.playerSide;
    state.gameState = params.gameState;
    emptyBoard();

    updateStatusMessage();
    updateBoardState();

    setJoinLink(state.gameId);
    if (!state.isBlack) {
        $("#hostscreen").toggle();
    }

    await(1000 * 60 * 5, 50);
}

function handleBoardUpdate(gameState) {
    state.gameState = gameState;

    updateStatusMessage();
    updateBoardState();
}

function updateStatusMessage() {
    let check = '';
    let text = '';

    $('#gamestatus').text('').css('opacity', 0);
    if (!state.gameState.gameFinished) {
        if (state.gameState.check) {
            check = ': ШАХ';
        }

        if (state.gameState.currentPlayer === 'WHITE') {
            text = "Ход белых" + check;
        } else {
            text = "Ход черных" + check;
        }
    } else {
        if (state.gameState.winner) {
            text = "Мат! Победа" + ((state.gameState.winner === 'WHITE') ? 'Белых!' : 'Черных');
        } else {
            text = "Ничья. Победила дружба.";
        }
    }

    setTimeout(() => {
        $('#gamestatus').text(text).css('opacity', 1);
    }, 300);
}

function setJoinLink(gameid) {
    $('#joinlink').text(window.location.origin + '?joinid=' + gameid);
}

function updateBoardState() {
    $("#hostscreen").hide();

    state.validMoves = {};

    cleanupPieces();
    cleanupSelections();

    state.gameState.pieces.forEach((p, i) => {
        setPiece(p);
        if (p.validMoves && p.validMoves.length > 0) {
            state.validMoves[p.position] = p.validMoves;
        }
    });
    highlightLastOppMove();
}

function highlightLastOppMove() {
    if (state.gameState.myTurn && state.gameState.lastOpponentMove) {
        const m = state.gameState.lastOpponentMove;
        atPosition(m.from).attr('data-piece-state', 'oppmove');
        atPosition(m.to).attr('data-piece-state', 'oppmove');
    }
}

function cleanupPieces() {
    foreachPosition(p => {
        atPosition(p).attr('data-piece', '');
    });
}

function cleanupSelections() {
    foreachPosition(p => {
        atPosition(p).attr('data-piece-state', '');
    });
}

function setPiece(piece) {
    const square = atPosition(piece.position);
    const dp = piece.pieceType.toLowerCase() + '-' + piece.color.toLowerCase();
    square.attr('data-piece', dp);
    square.attr('width', '200px');
}

function atPosition(pos) {
    return $('#board td[data-position=' + pos + ']');
}

function promoteQueen() {
    completePromotion('QUEEN');
}

function promoteRock() {
    completePromotion('ROCK');
}

function promoteBishop() {
    completePromotion('BISHOP');
}

function promoteKnight() {
    completePromotion('KNIGHT');
}

function completePromotion(pieceType) {
    $("#promotewhite").hide();
    $("#promoteblack").hide();


    move(state.selectedPosition, state.to, pieceType);
}


function foreachPosition(consumer) {
    let rowNums = [1, 2, 3, 4, 5, 6, 7, 8];
    let cols = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];

    rowNums.forEach((r, i) => {
        cols.forEach((c, j) => {
            consumer(c + r);
        });
    });
}

function emptyBoard() {
    const board = $("#board");
    let trs = [];

    let rowNums = [1, 2, 3, 4, 5, 6, 7, 8];
    let cols = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
    let styles = ['cell-black', 'cell-white'];

    if (state.isBlack) {
        cols.reverse();
    } else {
        rowNums.reverse();
    }

    board.append(topTr(cols));
    rowNums.forEach((r, i) => {
        styles.reverse();
        board.append(boardTr(r, cols, styles));
    });
    board.append(topTr(cols));
}

function topTr(cols) {
    let tr = $('<tr class="text-center">');
    tr.append($('<td>'));
    cols.forEach((c, i) => {
        tr.append($('<td>').text(c));
    });
    tr.append($('<td>'));
    return tr;
}

function boardTr(row, cols, styles) {
    let tr = $('<tr>');
    tr.append($('<td>').text(row));
    cols.forEach((c, i) => {
        let pos = c + row;
        tr.append($('<td>')
            .addClass('cell')
            .addClass(styles[i % 2])
            .attr('data-position', pos)
            .click(() => {
                handleBoardClick(pos);
            })
        );
    });
    tr.append($('<td>').text(row));
    return tr;
}

function handleBoardClick(pos) {
    if (atPosition(pos).attr('data-piece-state') === 'move') {
        handleMove(pos);
    } else {

        cleanupSelections();
        highlightLastOppMove();
        if (!state.gameState.gameFinished
            && state.gameState.myTurn
            && atPosition(pos).attr('data-piece')
            && atPosition(pos).attr('data-piece').includes(state.playerSide.toLowerCase())) {
            handleMyPieceClickOnMyMove(pos);
        }
    }

    // alert('clicked on ' + pos);
}

function handleMyPieceClickOnMyMove(pos) {
    atPosition(pos).attr('data-piece-state', 'selected');
    state.selectedPosition = pos;
    const validMoves = state.validMoves[pos];
    if (validMoves) {
        validMoves.forEach((m, i) => {
            atPosition(m).attr('data-piece-state', 'move');
        });
    }
}

function fillMeetingTable(meetings) {
    var mtgtbody = $('#meetingsTbody');

    mtgtbody.empty();

    meetings.forEach((m, i) => {
        let removeBtn = $('<a class="btn btn-danger" role="button">X</a>')
            .click(() => {
                dropMeeting(m.id);
            });

        let tr = $('<tr>')
            .append($('<td>').text(m.description))
            .append($('<td>').text(formatLocal(Date.parse(m.meetingTimeDate))))
            .append($('<td>').text(formatLocal(Date.parse(m.meetingTimeOffsetDateTime))))
            .append($('<td>').text(formatNy(Date.parse(m.meetingTimeDate))))
            .append($('<td>').text(formatNy(Date.parse(m.meetingTimeOffsetDateTime))))
            .append($('<td>').append(removeBtn));

        mtgtbody.append(tr);

    });
}





